package outils;

import jakarta.servlet.http.HttpSession;

public class MySession {
    
    HttpSession session;

    public Object get(String attributeName)
        {
            return this.getSession().getAttribute(attributeName); 
        }
    public void set(String attributeName,Object obj)
        {
           this.getSession().setAttribute(attributeName, obj);
        }
    public void delete(String attributeName)
        {
            this.getSession().removeAttribute(attributeName);
        }


    public HttpSession getSession() {
        return this.session;
    }

   
    public void setSession(HttpSession session) {
        this.session = session;
    }

    public MySession(HttpSession session)
        {
            this.setSession(session);
        }
    
}
