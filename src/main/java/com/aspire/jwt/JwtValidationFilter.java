package com.aspire.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Extract the JWT token from the request header
		String jwt= request.getHeader(SecurityConstants.JWT_HEADER);

		if(jwt != null) {
						
			try {

				// Remove the "Bearer " prefix from the token
				jwt = jwt.substring(7);

				// Create a secret key for JWT verification
				SecretKey key= Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

				// Parse and validate the JWT
				Claims claims= Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

				// Extract user information from the JWT claims
				String username= String.valueOf(claims.get("username"));
				String role= (String)claims.get("role");

				// Create a list of granted authorities based on the user's role
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority(role));

				// Create an authentication token
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

				// Set the authentication token in the SecurityContextHolder
				SecurityContextHolder.getContext().setAuthentication(auth);
				
			} catch (Exception e) {
				// If an exception occurs during JWT validation, throw a BadCredentialsException
				throw new BadCredentialsException("Invalid Token received..");
			}

		}
		
		filterChain.doFilter(request, response);
	}

	// Define which URLs should not be filtered by this validation filter
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		final String url = request.getServletPath();
		boolean isSignInUrl = url.equals("/signIn");
		boolean isGenerateOtpUrl = url.equals("/generateOtp");
		boolean isValidateOtpUrl = url.equals("/validateOtp");

		// Do not filter /signIn, /generateOtp, and /validateOtp URLs
		return (isSignInUrl || isGenerateOtpUrl || isValidateOtpUrl);
	}

}
