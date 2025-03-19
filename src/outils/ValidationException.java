package outils;

public class ValidationException extends Exception {
    ValueController valueController;
    String url;
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
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
