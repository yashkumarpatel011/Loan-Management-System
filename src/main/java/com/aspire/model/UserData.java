package com.aspire.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	@Size(min = 3,max = 20 , message = "Enter minimum 3 character and maximum 20 characters in full name.")
    private String fullName;
    
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Invalid password. It must contain at least 8 characters, including at least one digit, one lowercase letter, one uppercase letter, and one special character."
        )
    private String password;
    
    @Email(message = "Enter a valid Email.")
    private String email;
    
    private String role;

    private boolean isVerified;
    
}
