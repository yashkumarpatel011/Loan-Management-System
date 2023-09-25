package com.aspire.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.aspire.jwt.JwtGeneratorFilter;
import com.aspire.jwt.JwtValidationFilter;

import java.util.Properties;

@Configuration
public class SecurityConfig {
	
	@Bean
    SecurityFilterChain mySecurity(HttpSecurity http) throws Exception
	{
		
		http
	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    .csrf().disable()
	    .authorizeHttpRequests()
	    .requestMatchers("/app/**").permitAll()
	    .requestMatchers(HttpMethod.GET, "/logged-in/**").hasAnyRole("USER","ADMIN")
				.requestMatchers(PathRequest.toH2Console()).permitAll()
				.requestMatchers(HttpMethod.POST, "/loan/create").hasAnyRole("USER")
				.requestMatchers(HttpMethod.GET, "/loan/status/**").hasAnyRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/loan/get").hasAnyRole("ADMIN","USER")
				.requestMatchers(HttpMethod.PATCH, "/loan/reassign").hasAnyRole("ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/loan/paid").hasAnyRole("USER")
	    .anyRequest().authenticated()
	    .and()
	    .addFilterAfter(new JwtGeneratorFilter(), BasicAuthenticationFilter.class)
	    .addFilterBefore(new JwtValidationFilter(), BasicAuthenticationFilter.class)
	    .formLogin()
	    .and()
	    .httpBasic();


		http.headers(headersConfigurer -> headersConfigurer
				.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));

		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername("yashkumarpatel1997@gmail.com");
		mailSender.setPassword("<Need_to_add_password>");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

}
