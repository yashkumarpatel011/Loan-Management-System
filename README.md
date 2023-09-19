# Loan Management System with Security and JWT Tokens

Welcome to the documentation for the Loan Management System with Security and JWT Tokens. This system provides a secure and efficient way to manage loans and user accounts, ensuring that only authorized users can access and perform actions within the system.



## Installation and Setup

```
### Prerequisites
- Java Development Kit (JDK) 8 or later
- Maven
- Postman (for testing the API)
```

### 1. Clone the Repository

```
https://github.com/yashkumarpatel011/Loan-Management-System.git
```

### 2. Go the Project

```
cd Loan-Management-System

```

### 3. Run the Application
- For GitBash
```
./mvnw spring-boot:run

```
- For IDE
```
cd /src/main/java/com/aspire/AspireBackEndAssignment.java
Run as a Java application
```

**The application will start running on [http://localhost:8080](http://localhost:8081)**

## System Overview
The Loan Management System is designed to handle the following key features:

- **User Registration:** Users can register by providing their personal information, including name, email, and password. User passwords are securely hashed before storage.
- **User Validation:** Email OTP based verification happens to validate the user.
- **User Sign-In:** Registered users can sign in to their accounts using their email and password. Successful sign-in generates a JWT (JSON Web Token) for authentication.

- **Create Loan:** Users can create loan requests by specifying the loan amount and duration. Loan requests are stored securely in the system.

- **Loan Repayment:** Users can record loan repayment details, including the amount paid. The system keeps track of repayment history.

- **Get Loans:** Users can retrieve their own loan information, while administrators can view all loans in the system.

- **Loan Status Approval:** Administrators can approve or reject loan requests, updating their status accordingly.

- **Loan Reassignment:** Administrators can reassign loans to different users or administrators as needed.

## Authentication and Authorization
The system uses JWT (JSON Web Token) authentication to ensure secure access to its features. Here's how authentication and authorization are implemented:

- Users must register and sign in to obtain a JWT Token.
- JWT Tokens are used to authenticate users for each API request.
- Role-based access control is in place:
  - **ROLE_USER** can create loans and record repayments, as well as view their own loans.
  - **ROLE_ADMIN** can view all loans, approve loan requests, and reassign loans.

## High Level Diagram 

```
Loan_Management_Sequence_Diagram.png

```


## Tables ##

This API uses 3 tables to operate, USER_DATA , LOAN_DETAILS and REPAYMENT_DETAILS Table

### USER_DATA Table

| **Column Name** | **Data Type**   | **Comment**               |
|-----------------|-----------------|---------------------------|
| ID              | int             |                           |
| FULL_NAME       | varchar         | Yashkumar Patel           |
| EMAIL           | varchar, unique | abc123@gmail.com          |
| PASSWORD        | varchar         | Encrypted form            |
| ROLE            | varchar         | ex. ROLE_ADMIN, ROLE_USER |

### LOAN_DETAILS Table

| Column Name      | Data Type | Comment                |
|------------------|-----------|------------------------|
| ID               | int       |                        |
| USER_ID          | int       |                        |
| AMOUNT           | double    | 5000.0                 |
| DURATION         | int       | 3                      |
| REPAYMENT_FREQ   | varchar   | WEEKLY          |
| INTEREST_RATE    | int       | 0.0                    |
| STATUS           | varchar   | PENDING,PAID,APPROVED,etc.. |
| ASSIGNED         | int       | ADMIN ( User_ID)       |
| REMAINING_AMOUNT | double    | 5000.0                 |

### REPAYMENT_DETAILS Table

| Column Name  | Data Type | Comment                |
|--------------|-----------|------------------------|
| ID           | int       |                        |
| LOAN_ID      | int       |                        |
| AMOUNT       | double    | 5000.0                 |
| STATUS       | varchar   | PENDING,PAID,NOT_REQUIRED |
| SCHEDULED_ON | datetime  |                        |
| PAID_AMOUNT  | double    | 1000.0                 |

## Postman Collection

```
Loan Management System API's Collections.postman_collection.json

```

### Guide 
- Download the .json file 
- Import the .json in Postman 
- You can see the All API's in your postman_collection

## **API Endpoints**

### 1. User Signup

- **Method**: POST
- **Path**: `http://localhost:8080/app/signup`
- **Description**: Register a new user.
- **Request Details :** 
To create a new user account, send a POST request to http://localhost:8080/app/signup.
  The request body should be in JSON format and include the following fields:
- **Request Body**: 

```

{
  "fullName": "Yash Patel",
  "password": "Yash@123",
  "email": "yashkumarpatel1997@gmail.com"
}
```
- **fullName (string):** The full name of the user being registered.
- **password (string)**: The password chosen by the user for their account. Ensure it meets any security requirements.
- **email (string)**: The email address of the user. This will be used as the user's unique identifier.


- **Response:**

```
{
    SIGNUP_SUCCESSFULL
}

```

- **Example Usage**:

  To register a new user, make a POST request to http://localhost:8080/app/signup with the provided JSON request body. Upon successful registration, you will receive the successful registration msg.

### 2. Generate the OTP 

- **Method**: GET
- **Path**: `http://localhost:8080/generateOtp
- **Description**: This API allows you to generate a one-time password (OTP) for user authentication or verification purposes. To use this API, you need to provide authentication credentials..

- **Note : We will send the OTP to your registered email id.** 
- **Authentication :**`
  - To access this API, you must provide valid authentication credentials:
    
  - **Username:** yashkumarpatel1997@gmail.com
   - **Password:** Yash@123


- **Request Details :**
  To generate an OTP, send a GET request to the specified endpoint.  Here's a general request body example:
- **Response Body**:
```
  OTP Sent Successfully to your registered  email ID.
```
- **Note : OTP Expire time sets 10 minutes.** 

### 3. Validate the OTP 
- **Method:** GET
- **Path:** http://localhost:8080/validateOtp?otpnum=282927
- **Description**: This API allows you to validate a one-time password (OTP) for user authentication or verification purposes. To use this API, you need to provide authentication credentials and expect a JWT (JSON Web Token) token in the response header for successful validation.

- **Authentication :**`
  - To access this API, you must provide valid authentication credentials:

  - **Username:** yashkumarpatel1997@gmail.com
  - **Password:** Yash@123
- **Request Details :** To validate an OTP, send a GET request to the specified endpoint. You must include the OTP you want to validate as a query parameter. 
  - For example:
  ```
  http://localhost:8080/validateOtp?otpnum=282927

  ```
- **Query Parameter** 
  - otpnum (string): The OTP you want to validate.

- **Response :**
  - The API will respond with a status indicating whether the provided OTP is valid or not. If the OTP is valid, it will also include a JWT token in the response header for further authorized requests.
  - **Status:** 200 OK (Success)

    - **Response Header**: Authorization: Bearer <JWT Token>
    - **Response Body:** { "status": "Entered Otp is valid" }
  - **Status:** 401 Unauthorized (Authentication Failure)
    - **Response Body:** { "error": "Authentication failed" }
  - **Status:** 400 Bad Request (Invalid OTP)
    - **Response Body:** { "error": "Entered Otp is NOT valid. Please Retry!" }

- **Response Body :**
  - If the OTP is valid, the response will have a status field with the value "valid."
  - If the authentication credentials are invalid, the response will include an error message: "Authentication failed."
  - If the OTP is invalid or expired, the response will include an error message: "Invalid OTP."
- **Response Header :**
  - Upon successful OTP validation, the API will include a JWT token in the response header. You should include this token in the Authorization header of your subsequent authorized requests.

### 4 . User Login

- **Method**: GET
- **Path:** `http://localhost:8080/signIn`
- **Description:** This API allows you to authenticate and sign in a user with their provided credentials. To use this API, you need to provide authentication credentials.

- **Authentication:** Basic Authentication (Username and Password)
    - Username: [yashkumarpatel1997@gmail.com](mailto:sk@gmail.com)
    - Password: Yash@123
- **Request Details :** To sign in a user, send a GET request to the specified endpoint. You must include the authentication credentials as part of the request.
  - For example:
  ```
  http://localhost:8080/signIn

  ```
- **Request Headers :** 
Include the following headers in your request:

  - **Authorization:** Basic authentication with the provided username and password.

- **Response:**
  - The API will respond with a status indicating whether the provided credentials are valid or not.

  - **Status:** 200 OK (Success)

    - **Response Body:** { "status": "LOGIN_SUCCESSFUL" }
  - **Status:** 401 Unauthorized (Authentication Failure)

    
```
{
    LOGIN_SUCCESSFUL
}

```

### 5. Checking the Role 

- **Method:** GET
- **Path:** `http://localhost:8080/logged-in/user`
- **Description:** This API allows you to check the role of a logged-in user by providing a JWT (JSON Web Token) Token for authentication.
- **Authentication:** Bearer Token
- **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to check the role of the logged-in user. It does not require a request body. The presence of a valid JWT Token is sufficient for authentication.
  ```
  http://localhost:8080/logged-in/user

  ```
- **Response:**
  - The API will respond with the role of the logged-in user.

  - **Status:** 200 OK (Success)

    - **Response Body:** { "status": "ROLE_USER" }
  - **Status:** 401 Unauthorized (Authentication Failure)

- **Response Body**
  - If the JWT Token is valid, the response will have a role field with the user's role (e.g., "ROLE_USER").
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."

- **Example:**
    - Bearer Token: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJZYXNoIiwic3ViIjoiSldUIFRva2VuIiwidXNlcm5hbWUiOiJ5YXNoa3VtYXJwYXRlbDE5OTdAZ21haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTY5NTAzMTk0NCwiZXhwIjoxNjk1MDYxOTQ0fQ.uCre0vIGxY9e7eLVTFzEkMKdilMKXMn1xE_ms0J-kCY
    - Response: Welcome to Yash's API World: Yash Patel role: ROLE_USER

### 6. Creating the Loan

- **Method:** POST
- **Path:** `http://localhost:8080/loan/create`
- **Description:** This API allows users to create a loan request by providing the loan amount and duration. To use this API, you need to provide a valid JWT (JSON Web Token) Token for authentication.
- **Note :**
  - **Once USER has created the loan then Loan is automatically assigned to the available Admin User for approval.**
  - **We are scheduled the repayment details at this stage.** 
- **Authentication:** Bearer Token
  - **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to create a loan request. Users should provide the requested loan amount and the duration for which the loan is needed. The request body should be in JSON format.

- **Request Body :**
  - **amount (float):** The loan amount requested by the user (e.g., 1000.0).
  - **duration (integer):** The duration for which the loan is requested in months (e.g., 3).
```
 {
    "amount": 1000.0,
    "duration": 3
}

  ```
- **Response:**
  - The API will respond with the result of the loan creation.

  - **Status:** 201 CREATED (Success)
    - **Response Body:**
  ```
  {
      "id": 1,
      "userId": 1,
      "amount": 1000.0,
      "duration": 3,
      "rePaymentFreq": "WEEKLY",
      "interestRate": 0.0,
      "status": "PENDING",
      "adminId": null,
      "remainingAmount": 1000.0,
      "repaymentDetails": null
  }
  
    ```

  - **Status:** 401 Unauthorized (Authentication Failure)

- **Response Body**
  - If the JWT Token is valid and the loan request is created successfully, the response will include the loan details as shown in the example above. 
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."

### 7. Approval of the Loan
- **Method:** GET
- **Path:** `http://localhost:8080/loan/status/approved?loanId=1`
- **Description**: This API allows users with the role ROLE_ADMIN to approve the status of a loan request. To use this API, you need to provide a valid JWT (JSON Web Token) Token with ROLE_ADMIN privileges for authentication.
- **Note:** Loan must be assigned to the login Admin User
- **Authentication:** Bearer Token
  - **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to approve the status of a loan request. Only users with the role ROLE_ADMIN are authorized to approve loan requests.
  ```
  http://localhost:8080/validateOtp?otpnum=282927

  ```
- **Query Parameter**
  - **loanId** (integer): The unique identifier of the loan request that needs approval.

- **Response :**
  - The API will respond with the result of the loan status approval.
  - **Status:** 200 OK (Success)
    - **Response Body:** { "status": "Loan status approved successfully" }
  - **Status:** 401 Unauthorized (Authentication Failure)
    - **Response Body:** { "error": "Authentication failed" }
  - **Status:** 400 Bad Request 
    - **Response Body:** { "error": "Loan is assigned to different admin" }

- **Response Body :**
  - If the JWT Token with ROLE_ADMIN privileges is valid and the loan status is approved successfully, the response will include a success message: "Loan status approved successfully."
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."
  - If the user have the required ROLE_ADMIN privileges, but Loan is assigned to different admin then message looks like "Loan is assigned to different admin". 

### 8. Reassign the Loan
- **Method:** PATCH
- **Path:** `http://localhost:8080/loan/reassign?loanId=2`
- **Description**: This API allows users with the role ROLE_ADMIN to reassign a loan request. To use this API, you need to provide a valid JWT (JSON Web Token) Token with ROLE_ADMIN privileges for authentication.
- **Note:** To Reassign the Loan, Login user must be have the ROLE_ADMIN privilege
- **Authentication:** Bearer Token
  - **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to reassign a loan request to a different user or admin. Only users with the role ROLE_ADMIN are authorized to access and perform loan reassignment.
  ```
  http://localhost:8080/loan/reassign?loanId=2

  ```
- **Query Parameter**
  - **loanId** (integer): The unique identifier of the loan request that needs approval.

- **Response :**
  - The API will respond with the result of the loan status approval.
  - **Status:** 200 OK (Success)
    - **Response Body:** { "status": "Loan reassigned successfully" }
  - **Status:** 401 Unauthorized (Authentication Failure)
    - **Response Body:** { "error": "Authentication failed" }

- **Response Body :**
  - If the JWT Token with ROLE_ADMIN privileges is valid and the loan is reassigned successfully, the response will include a success message: "Loan reassigned successfully."
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."

### 9. Get the Loan Details
- **Method:** GET
- **Path:** `http://localhost:8080/loan/get`
- **Description**: This API allows users with the role ROLE_USER to see their own loans, while users with the role ROLE_ADMIN can see all loans. To use this API, you need to provide a valid JWT (JSON Web Token) Token with the appropriate privileges for authentication.
- **Note:** ROLE_USER can only see the own loans and ROLE_ADMIN can see the all the loans.
- **Authentication:** Bearer Token
  - **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to retrieve loan information based on the user's role. ROLE_USER can only see their own loans, while ROLE_ADMIN can see all loans.
  ```
  http://localhost:8080/loan/get
  ```
- **Response :**
  - The API will respond with a list of loans based on the user's role.
  - **Status:** 200 OK (Success)
    - **Response Body:** 
      JSON Response of Loans and Repayment Details
  - **Status:** 401 Unauthorized (Authentication Failure)
    - **Response Body:** { "error": "Authentication failed" }

- **Response Body :**
  - If the JWT Token with the appropriate role-based privileges is valid, the response will include a list of loans. Users with ROLE_USER will see their own loans, while users with ROLE_ADMIN will see all loans.
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."

### 10. Repayment Loan Details
- **Method:** PATCH
- **Path:** `http://localhost:8080/loan/paid?loanId=1&amount=500`
- **Description**:  This API allows users with the role ROLE_USER to record loan repayment details. Only ROLE_USER is authorized to access and use this API. To use this API, you need to provide a valid JWT (JSON Web Token) Token with ROLE_USER privileges for authentication.
- **Note:** To Repayment the Loan, Login user must be have the ROLE_USER privilege
- **Authentication:** Bearer Token
  - **Request Header:**
    - **Authorization:** Bearer <Your_JWT_Token>
- **Request Details :** This API is used to record loan repayment details. Users with the role ROLE_USER can update the status of their loan repayments.
  ```
  http://localhost:8080/loan/paid?loanId=1&amount=500
  ```
- **Query Parameter**
  - **loanId** (integer): The unique identifier of the loan request that needs approval.
  - **amount** (float): The amount being paid towards the loan.
- **Response :**
  - The API will respond with the result of the loan repayment update.
  - **Status:** 200 OK (Success)
    - **Response Body:** { "status": "Loan repayment recorded successfully" }
  - **Status:** 401 Unauthorized (Authentication Failure)
    - **Response Body:** { "error": "Authentication failed" }

- **Response Body :**
  - If the JWT Token with ROLE_USER privileges is valid and the loan repayment is recorded successfully, the response will include a success message: "Loan repayment recorded successfully."
  - If the JWT Token is invalid or expired, the response will include an error message: "Unauthorized."

### Tech Stack

- Java
- Spring Boot
- H2 Database
- Spring Security
- JWT Token
- Lombok
- Spring Test
- Spring Mail
- Maven

### Validation Rules

The following validation rules are applied to the user entity:

- Full Name:
    - Minimum length: 3 characters
    - Maximum length: 20 characters
- Password:
    - At least 8 characters
    - Contains at least one digit
    - Contains at least one lowercase letter
    - Contains at least one uppercase letter
    - Contains at least one special character
- Email:
    - Valid email format
- Role:
  - ROLE_USER
  - ROLE_ADMIN

### Development

The project can be imported and run using an IDE like Intellij .

### Test API

You can use Postman to test the API endpoints.

## H2 Database Configuration

The project uses the H2 in-memory database by default.

The application is configured to use the H2 database. The configuration can be found in the `application.properties` file:

```
# Server Port Configuration
server.port=8080

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

```

## **Contributors**

- **[Yashkumar Patel]**
 
