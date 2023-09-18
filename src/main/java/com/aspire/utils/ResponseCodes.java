package com.aspire.utils;

public enum ResponseCodes {

    LOAN_COMPLETED("LOAN_COMPLETED"),
    SCHEDULED_PAYMENT_COMPLETED("SCHEDULED_PAYMENT_COMPLETED"),
    LESS_AMOUNT("LESS_AMOUNT_THAN_SCHEDULED_PAYMENT"),

    LOGIN_SUCCESSFUL("LOGIN_SUCCESSFUL"),

    SIGN_UP_FIRST("PLEASE_SIGNUP_BEFORE_LOGIN"),

    VERIFY_EMAIL_LOGIN("VERIFY_EMAIL_TO_LOGIN"),

    OTP_VALID("Entered Otp is valid"),

    OTP_INVALID("Entered Otp is NOT valid. Please Retry!"),

    LOGIN_NECESSARY("Please Signup or Verify the Email"),
    INVALID_ROLE("Kindly Select Role as ROLE_USER or ROLE_ADMIN"),

    SIGNUP_SUCCESSFULL("SIGNUP_SUCCESSFULL"),

    USER_ALREADY_EXIST("USER_ALREADY_EXIST");



    private String value;

    private ResponseCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
