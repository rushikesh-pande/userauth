# User Authentication Library

A reusable Spring Boot authentication library providing JWT-based authentication for microservices.

## Features

- JWT token generation and validation
- User authentication and authorization
- Password encryption using BCrypt
- Stateless session management
- Spring Security integration
- Easy integration with microservices

## Usage

### 1. Add as Maven Dependency

```xml
<dependency>
    <groupId>com.auth</groupId>
    <artifactId>userauth</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Enable Component Scanning

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.your.package", "com.auth.userauth"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. Use Authentication Service

```java
@Autowired
private AuthenticationService authService;

// Authenticate user
AuthRequest request = new AuthRequest("username", "password");
AuthResponse response = authService.authenticate(request);
String token = response.getToken();

// Validate token
Optional<User> user = authService.validateTokenAndGetUser(token);
```

### 4. Protect Endpoints

Add `Authorization: Bearer <token>` header to requests.

## Default Users

- Username: `admin`, Password: `admin123`, Role: `ADMIN`
- Username: `user`, Password: `user123`, Role: `USER`

## API

### AuthenticationService
- `authenticate(AuthRequest)`: Authenticate user and get JWT token
- `registerUser(username, email, password, role)`: Register new user
- `validateTokenAndGetUser(token)`: Validate token and get user details
- `changePassword(username, oldPassword, newPassword)`: Change user password

### JwtService
- `generateToken(User)`: Generate JWT token
- `validateToken(String)`: Validate JWT token
- `extractUsername(String)`: Extract username from token
- `extractRole(String)`: Extract role from token

## Configuration

Configure in `application.properties`:

```properties
jwt.secret=YourSecretKey
jwt.expiration=86400000
```

## Security

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours (configurable)
- Stateless authentication
- CSRF protection disabled for API use

