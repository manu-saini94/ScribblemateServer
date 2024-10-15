package com.scribblemate.configuration;

import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

	public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver,
			JwtAuthenticationService jwtService, UserDetailsService userDetailsService) {
		this.handlerExceptionResolver = handlerExceptionResolver;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		// Extract accessToken from cookies
		Cookie[] cookies = request.getCookies();
		String jwt = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					jwt = cookie.getValue();
					break;
				}
			}
		}

		// If accessToken is missing, proceed with the filter chain
		if (jwt == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// Extract the user email from the JWT token
			String userEmail = jwtService.extractUsername(jwt);

			// Check if the user is already authenticated
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (userEmail != null && authentication == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

				// Validate the JWT token
				if (jwtService.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			// Continue with the filter chain
			filterChain.doFilter(request, response);

		} catch (Exception exception) {
			// Handle any exceptions that occur during authentication
			handlerExceptionResolver.resolveException(request, response, null, exception);
		}
	}

}
