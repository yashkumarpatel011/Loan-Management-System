package com.aspire.controller;

import com.aspire.model.UserData;
import com.aspire.service.Impl.EmailService;
import com.aspire.service.Impl.OTPService;
import com.aspire.service.UserService;
import com.aspire.utils.ResponseCodes;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.aspire.utils.EmailTemplate;

import java.util.Map;
import java.util.HashMap;

@RestController
public class OTPController {


    @Autowired
    public OTPService otpService;

    @Autowired
    public EmailService emailService;

    @Autowired
    public UserService userService;

    @GetMapping("/generateOtp")
    public ResponseEntity<String> generateOTP() throws MessagingException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        int otp = otpService.generateOTP(username);
        //Generate The Template to send OTP
        EmailTemplate template = new EmailTemplate("com/aspire/utils/SendOtp.html");
        Map<String,String> replacements = new HashMap<String,String>();
        replacements.put("user", username);
        replacements.put("otpnum", String.valueOf(otp));
        String message = template.getTemplate(replacements);
        emailService.sendOtpMessage(username, "OTP -SpringBoot", message);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @GetMapping(value ="/validateOtp")
    public ResponseEntity<String> validateOtp(@RequestParam("otpnum") int otpnum){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        //Validate the Otp
        if(otpnum >= 0){

            int serverOtp = otpService.getOtp(username);
            if(serverOtp > 0){
                if(otpnum == serverOtp){

                    // Convert the Temp user to Actual User
                    UserData user = userService.fetchUser(username);

                    if (user == null) {
                        return new ResponseEntity<>(ResponseCodes.SIGN_UP_FIRST.getValue(),HttpStatus.OK);
                    }
                    userService.validateUser(user);

                    otpService.clearOTP(username);

                    return new ResponseEntity<>(ResponseCodes.OTP_VALID.getValue(),HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(ResponseCodes.OTP_INVALID.getValue(),HttpStatus.BAD_REQUEST);
                }
            }else {
                return new ResponseEntity<>(ResponseCodes.OTP_INVALID.getValue(),HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>(ResponseCodes.OTP_INVALID.getValue(),HttpStatus.BAD_REQUEST);
        }
    }
}
