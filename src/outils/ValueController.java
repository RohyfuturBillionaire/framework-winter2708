package outils;

import java.util.HashMap;

public class ValueController {
 private HashMap<String, ErrorMessage> errorsMessage= new HashMap<String, ErrorMessage>();    
    public void setErrorsMessage(HashMap<String, ErrorMessage> errorsMessage) {
        this.errorsMessage = errorsMessage;
    }

    public HashMap<String, ErrorMessage> getErrorsMessage() {
        return errorsMessage;
    }
    public void add(String key, ErrorMessage value) {
        this.errorsMessage.put(key, value);
    }
}