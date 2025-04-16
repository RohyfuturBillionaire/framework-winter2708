package outils;

import java.util.HashMap;

public class User {
    String username;
    String role;
 private HashMap<String,Object> info= new HashMap<String,Object>();

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public void addInfo(String keyName,Object value){
        this.info.put(keyName, value);
    }

    public Object getInfo(String keyName)
    {
        return this.info.get(keyName);
    }


}
