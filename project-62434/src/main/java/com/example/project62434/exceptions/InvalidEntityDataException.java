package com.example.project62434.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvalidEntityDataException extends RuntimeException {

    private List<String> violations;

    public InvalidEntityDataException(String message) {
        super(message);
    }

    public InvalidEntityDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEntityDataException(Throwable cause) {
        super(cause);
    }

    public InvalidEntityDataException(String message, List<String> violations) {
        super(message);
        this.violations = violations;
    }

    public InvalidEntityDataException(String message, Throwable cause, List<String> violations) {
        super(message, cause);
        this.violations = violations;
    }

    public InvalidEntityDataException(Throwable cause, List<String> violations) {
        super(cause);
        this.violations = violations;
    }
}
