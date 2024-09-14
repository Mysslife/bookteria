package com.devteria.identity.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.devteria.identity.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    @DobConstraint(
            min = 10,
            message = "INVALID_DOB") // khi error xảy ra -> sẽ tự động văng MethodArgumentNotValidException -> sẽ được
    // hứng và xử lý bởi class GlobalExceptionHandler
    LocalDate dob;
}
