package outils;

import java.lang.reflect.Method;

public class VerbMethod {
    Method method;
    String verb;
    

    public Method getMethod() {
        return method;
    }

    public String getVerb() {
        return verb;
    }


    public Method setMethod(String Method) {
        return method;
    }

    public VerbMethod(Method method, String verb) {
        this.setMethod(method);;
        this.setVerb(verb);;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VerbMethod that = (VerbMethod) o;

        return getVerb() != null ? getVerb().equals(that.getVerb()) : that.getVerb() == null;
    }


}
