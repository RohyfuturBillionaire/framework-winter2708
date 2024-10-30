package outils;

import java.util.Set;

public class Mapping {
        String className;
        Set<VerbMethod> verbmethods= new CustomSet<>();
        
        
        public void setVerbmethods(Set<VerbMethod> verbmethods) {
            this.verbmethods = verbmethods;
        }
        
        public String getClassName() {
            return className;
        }
        
        public void setClassName(String className) {
            this.className = className;
        }

        public Set<VerbMethod> getVerbmethods() {
            return verbmethods;
        }
        
       public Mapping(String classNames){
            this.className=className;
            
        }
        
        
    }

