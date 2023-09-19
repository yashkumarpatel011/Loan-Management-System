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

    USER_ALREADY_EXIST("USER_ALREADY_EXIST"),
    REPAYMENT_PENDING("Your Loan is not approved yet. Kindly wait to approve the loan"),

    REPAYMENT_PAID("You don't have any due payment."),

    REPAYMENT_REJECTED("Your Loan is rejected by user admin user. Kindly contact the customer care for more information"),

    REPAYMENT_CANCELLED("You have cancelled the loan."),

    NOT_HAVE_LOAN("You don't have any loan with us."),

    LOGIN_BEFORE_LOAN_CREATION("Please login before creating Loan"),

    LOAN_APPROVED("Loan has been approved"),

    LOAN_STATUS_NOT_VALID("Loan Status is not accepted"),

    LOAN_ASSIGNED_DIFF_ADMIN("Unable to approve the loan because loan assigned to different admin"),

    LOGIN_BEFORE_LOAN_APPROVED("Please login before approving Loan"),

    GET_LOAN_PERMISSION_DENIED("You don't have any permission to view the loan details"),

    LOGIN_BEFORE_GET_LOAN("Please login before fetching Loan Detials"),

    ASSIGNED_UPDATED("Assigned Updated Successfully");



    private String value;

    private ResponseCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
