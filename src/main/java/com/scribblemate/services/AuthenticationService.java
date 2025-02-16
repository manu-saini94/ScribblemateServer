package com.scribblemate.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.auth.TokenDeletionException;
import com.scribblemate.exceptions.auth.TokenExpiredException;
import com.scribblemate.exceptions.auth.TokenMissingOrInvalidException;
import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserInactiveException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.RefreshTokenRepository;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.UserUtils;
import com.scribblemate.utility.Utils.Status;
import com.scribblemate.utility.Utils.TokenType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {

    private Random random = new Random(1000);

    @Value("${security.jwt.refresh-expiration-time}")
    private Long refreshTokenDurationMs;

    @Value("${security.jwt.access-expiration-time}")
    private Long accessTokenDurationMs;

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtAuthenticationService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User signUp(RegistrationDto input) {
        Optional<User> existingUser = userRepository.findByEmail(input.getEmail());
        if (existingUser.isPresent())
            throw new UserAlreadyExistException();
        User newUser = null;
        try {
            newUser = new User().setFullName(input.getFullName()).setEmail(input.getEmail())
                    .setPassword(passwordEncoder.encode(input.getPassword())).setStatus(Status.ACTIVE);
            return userRepository.save(newUser);
        } catch (Exception exp) {
            log.error(UserUtils.ERROR_PERSISTING_USER, newUser);
            throw new RegistrationException();
        }
    }

    public User authenticate(LoginDto input) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = new User();
        List<Object[]> fieldsList = userRepository.findSpecificFieldsByEmail(input.getEmail());
        if (fieldsList != null) {
            for (Object[] row : fieldsList) {
                Long id = (Long) row[0];
                LocalDateTime createdAt = (LocalDateTime) row[1];
                String email = (String) row[2];
                String fullName = (String) row[3];
                String profilePicture = (String) row[4];
                Status status = (Status) row[5];
                LocalDateTime updatedAt = (LocalDateTime) row[6];
                user.setId(id);
                user.setCreatedAt(createdAt);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setProfilePicture(profilePicture);
                user.setStatus(status);
                user.setUpdatedAt(updatedAt);
            }
            return user;
        } else {
            log.error(UserUtils.ERROR_USER_NOT_FOUND);
            throw new UserNotFoundException();
        }
    }

    public User authenticate(@RequestBody LoginDto loginUserDto, HttpServletResponse response) {
        User authenticatedUser = authenticate(loginUserDto);
        if (authenticatedUser.getStatus().equals(Status.INACTIVE))
            throw new UserInactiveException();
        setTokensAndCookies(authenticatedUser, response);
        return authenticatedUser;
    }

    public void setTokensAndCookies(User user, HttpServletResponse response) {
        String jwtAccessToken = jwtService.generateToken(user);
        Cookie newAccessTokenCookie = createAndReturnCookieWithAccessToken(jwtAccessToken);
        RefreshToken refreshToken = createRefreshToken(user);
        Cookie newRefreshTokenCookie = createAndReturnCookieWithRefreshToken(refreshToken);
        addCookies(response, newAccessTokenCookie, newRefreshTokenCookie);
    }

    public Cookie createAndReturnCookieWithRefreshToken(RefreshToken token) {
        Cookie newRefreshTokenCookie = new Cookie(TokenType.REFRESH_TOKEN.getValue(), token.getToken());
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setSecure(true);
        newRefreshTokenCookie.setMaxAge((int) (refreshTokenDurationMs / 1000));
        return newRefreshTokenCookie;
    }

    public Cookie createAndReturnCookieWithAccessToken(String token) {
        Cookie newAccessTokenCookie = new Cookie(TokenType.ACCESS_TOKEN.getValue(), token);
        newAccessTokenCookie.setHttpOnly(true);
        newAccessTokenCookie.setSecure(true);
        newAccessTokenCookie.setPath("/");
        newAccessTokenCookie.setMaxAge((int) (accessTokenDurationMs / 1000));
        return newAccessTokenCookie;
    }

    public void addCookies(HttpServletResponse response, Cookie... cookies) {
        for (Cookie cookie : cookies) {
            StringBuilder cookieHeader = new StringBuilder();
            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; Max-Age=")
                    .append(cookie.getMaxAge()).append("; Path=").append(cookie.getPath());
//			if ("refreshToken".equals(cookie.getName())) {
//				cookieHeader.append("; HttpOnly");
//			}
            cookieHeader.append("; SameSite=none; Secure");
            response.addHeader("Set-Cookie", cookieHeader.toString());
        }
    }

    public boolean forgot(String email) {
        int otp = random.nextInt(10000);
        String subject = "OTP from notesy";
        String message = "<h1> OTP = " + otp + "</h1>";
        String to = email;
        boolean flag = emailService.sendEmail(subject, message, to);
        return flag;
    }

    public User refreshAuthToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshTokenValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TokenType.REFRESH_TOKEN.getValue().equals(cookie.getName())) {
                    refreshTokenValue = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshTokenValue == null) {
            throw new TokenMissingOrInvalidException("Refresh token is missing or invalid");
        }
        Optional<RefreshToken> tokenOptional = getRefreshToken(refreshTokenValue);
        User user = tokenOptional.get().getUser();
        if (tokenOptional.isEmpty()) {
            throw new TokenMissingOrInvalidException("Refresh token missing from database");
        } else {
            deleteTokenForUser(user);
        }
        if (isRefreshTokenExpired(tokenOptional.get())) {
            throw new TokenExpiredException("Refresh token has expired");
        }
        setTokensAndCookies(user, response);
        return user;
    }

    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(User authUser) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepository.findByEmail(authUser.getEmail()).get();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtAuthenticationService.generateRefreshToken(user));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public int deleteTokenForUser(User user) {
        try {
            return refreshTokenRepository.deleteByUser(user);
        } catch (DataAccessException dae) {
            log.error("Failed to delete token due to database error.");
            throw new TokenDeletionException("Failed to delete token due to database error.");
        } catch (IllegalArgumentException iae) {
            log.error("Invalid user provided for token deletion.");
            throw new TokenDeletionException("Invalid user provided for token deletion.");
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting tokens.");
            throw new TokenDeletionException("An unexpected error occurred while deleting tokens.");
        }
    }

    public void logoutAuthUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookiesArray = request.getCookies();
        User user = null;
        if (cookiesArray != null) {
            for (Cookie cookie : cookiesArray) {
                if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                    if ("accessToken".equals(cookie.getName())) {
                        String accessTokenString = cookie.getValue();
                        user = userService.getUserFromJwt(accessTokenString);
                    }
                    Cookie invalidCookie = new Cookie(cookie.getName(), null);
//					invalidCookie.setHttpOnly("refreshToken".equals(cookie.getName())); // HttpOnly for refresh token
                    invalidCookie.setPath("/");
                    invalidCookie.setMaxAge(0);
                    response.addCookie(invalidCookie);
                }
            }
        }
        if (user != null) {
            deleteTokenForUser(user);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        SecurityContextHolder.clearContext();
    }
}
