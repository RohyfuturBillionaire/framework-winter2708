package outils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationException extends Exception {
    
    private Map<String,List<String>> errors;
    String errorUrl;
    String errorMethod;
    Map<String, String[]> paramsBeforeError;
    
    public ValidationException(Map<String,List<String>> errors) {
        super("Validation error");
        this.errors= errors;
    }

    public void setErrorMethod(String errorMethod) {
        this.errorMethod = errorMethod;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
    public void setParamsBeforeError(Map<String, String[]> paramsBeforeError) {
        this.paramsBeforeError = paramsBeforeError;
    }



    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
