package outils;

public class ValidationException extends Exception {
    ValueController valueController;
    public void setValueController(ValueController valueController) {
        this.valueController = valueController;
    }

    public ValueController getValueController() {
        return valueController;
    }
    public ValidationException() {
        super();
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

    public ValidationException(ValueController valueController) {
        this.valueController = valueController;
    }

}
