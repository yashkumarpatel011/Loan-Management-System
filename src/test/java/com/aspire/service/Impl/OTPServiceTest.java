package com.aspire.service.Impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
public class OTPServiceTest {

    @InjectMocks
    OTPService otpService;

    @Test
    public void testGenerateOTP() throws Exception {

        int result = otpService.generateOTP("test");
        assertTrue("OTP Generated", result !=0);
    }

    @Test
    public void testGetOTP() throws Exception {
        int result = otpService.generateOTP("test");
        assertEquals("OTP Matched", result,otpService.getOtp("test"));
    }

    @Test
    public void testClearOTP() throws Exception {
        int result = otpService.generateOTP("test");
        otpService.clearOTP("test");

        int getOTP = otpService.getOtp("test");

        assertEquals("OTP Cleared", 0, getOTP);
    }
}
