package com.aspire.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
public class JwtGeneratorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		log.info("inside doFilter....");

		// Check if there is an authenticated user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
        	
        	log.info("authentication {}" , authentication);

            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes());

			// Build the JWT token
            String jwt = Jwts.builder()
            		.setIssuer("Yash")
            		.setSubject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("role",getRole(authentication.getAuthorities()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime()+ 900000)) // expiration time
                    .signWith(key).compact();

			// Attach the JWT to the response header
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

		// Continue with the filter chain
        filterChain.doFilter(request, response);
	}

	  // Helper method to extract the role from GrantedAuthority
	  private String getRole(Collection<? extends GrantedAuthority> collection) {
	        
	    String role="";
		  for(GrantedAuthority ga:collection) {
			  role= ga.getAuthority();
		  }
	    
		  return role;
	    }
				
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		// Define which URLs should not be filtered by this generator filter
        return !request.getServletPath().equals("/validateOtp");
	}

}
