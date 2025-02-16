package com.scribblemate.configuration;

import java.io.IOException;

import com.scribblemate.entities.User;
import com.scribblemate.exceptions.auth.TokenExpiredException;
import com.scribblemate.exceptions.auth.TokenMissingOrInvalidException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.Utils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.scribblemate.services.JwtAuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtAuthenticationService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${app.api.prefix}")
    private String uriPrefix;
    private RequestMatcher skipMatcher;

    @Autowired
    private UserRepository userRepository;

    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver,
                                   JwtAuthenticationService jwtService, UserDetailsService userDetailsService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        skipMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(uriPrefix + Utils.REGISTER_URI),
                new AntPathRequestMatcher(uriPrefix + Utils.FORGOT_URI),
                new AntPathRequestMatcher(uriPrefix + Utils.LOGIN_URI)
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skipMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }
            Cookie[] cookiesArray = request.getCookies();
            String accessTokenString = null;
            String refreshTokenString = null;
            if (cookiesArray != null) {
                for (Cookie cookie : cookiesArray) {
                    if (Utils.TokenType.ACCESS_TOKEN.getValue().equals(cookie.getName())) {
                        accessTokenString = cookie.getValue();
                        if (accessTokenString == null) {
                            throw new TokenMissingOrInvalidException("Access token not found in request cookies");
                        } else if (!jwtService.isAccessToken(accessTokenString)) {
                            throw new TokenMissingOrInvalidException("Access token is Invalid!");
                        } else if (jwtService.isTokenExpired(accessTokenString)) {
                            throw new TokenExpiredException("Access token has expired!");
                        }
                    } else if (Utils.TokenType.REFRESH_TOKEN.getValue().equals(cookie.getName())) {
                        refreshTokenString = cookie.getValue();
                        if (refreshTokenString == null) {
                            throw new TokenMissingOrInvalidException("Refresh token not found in request cookies");
                        } else if (!jwtService.isRefreshToken(refreshTokenString)) {
                            throw new TokenMissingOrInvalidException("Refresh token is Invalid!");
                        } else if (jwtService.isTokenExpired(refreshTokenString)) {
                            throw new TokenExpiredException("Refresh token has expired!");
                        }
                    }
                }
            } else {
                throw new TokenMissingOrInvalidException("Cookies are missing from the request");
            }
            if (accessTokenString != null) {
                String userEmail = jwtService.extractUsername(accessTokenString);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (userEmail != null && authentication == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    if (jwtService.isTokenValid(accessTokenString, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    public User getUserFromJwt(String jwt) {
        final String userEmail = jwtService.extractUsername(jwt);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));
        return user;
    }


}
