package outils;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import outils.ValidationAnnotation.Max;
import outils.ValidationAnnotation.Min;
import outils.ValidationAnnotation.NotNull;
import outils.ValidationAnnotation.Size;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ControllerUtils {
    public Object parse(Object o, Class<?> typage) {
        if (typage.equals(int.class)) {
            return Integer.parseInt((String) o);
        } else if (typage.equals(double.class)) {
            return Double.parseDouble((String) o);
        } else if (typage.equals(boolean.class)) {
            return Boolean.parseBoolean((String) o);

        } else if (typage.equals(byte.class)) {
            return Byte.parseByte((String) o);

        } else if (typage.equals(float.class)) {
            return Float.parseFloat((String) o);

        } else if (typage.equals(short.class)) {
            return Short.parseShort((String) o);

        } else if (typage.equals(long.class)) {
            return Long.parseLong((String) o);

        }
        return typage.cast(o);
    }

    public boolean checkSessionNeed(Method method)
        {
            for (Parameter iterable_element : method.getParameters()) {
                
                if (iterable_element.getClass().equals(MySession.class)) {
                    return true;
                }
            }
            return false;
        }
    public static Object checkSession(Class<?> clas,HttpSession reqSession) throws Exception
        {
                Field [] flds= clas.getDeclaredFields();
                Object caller=clas.getDeclaredConstructor().newInstance((Object[])null);
                for (Field field : flds) {
                        if (field.getType().equals(MySession.class)) {
                            caller=clas.getDeclaredConstructor(MySession.class).newInstance( new MySession(reqSession));
                            
                        }
                }
            return caller;
        }

    public  List<String> getAllAnnoted(String packageToScan,Class annotation) throws Exception{

            
            String path = getClass().getClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File packageDir = new File(decodedPath);
            List<String> name= new ArrayList<>();
            // Parcourir tous les fichiers dans le répertoire du package
            File[] files = packageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageToScan + "." + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation)) {
                            name.add(clazz.getSimpleName());
                        }
                    }
                }
            }
        return name;
    }

    

    public static void getAllClassesSelonAnnotation(String packageToScan,Class<?>annotation,HashMap<String,Mapping> map) throws Exception{
        //List<String> controllerNames = new ArrayList<>();
        HashMap<String,Mapping> hm=map;
         //String path = getClass().getClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String path = Thread.currentThread().getContextClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File packageDir = new File(decodedPath);

            File[] files = packageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageToScan + "." + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation.asSubclass(java.lang.annotation.Annotation.class))) {
                            Method[]methods=clazz.getDeclaredMethods();
                            for (Method meth : methods) {
                                  
                                if (meth.isAnnotationPresent(Url.class)) {
                                    Url getAnnotation= meth.getAnnotation(Url.class);
                                    
                                    
                                    if (meth.isAnnotationPresent(Get.class)) {
                                        if(hm.containsKey(getAnnotation.path())){
                                        
                                            hm.get(getAnnotation.path()).getVerbmethods().add(new VerbMethod(meth,"GET"));
                                        
                                        }
                                        else{
                                            Mapping m=new Mapping(clazz.getName());
                                            m.getVerbmethods().add(new VerbMethod(meth,"GET"));
                                            hm.put(getAnnotation.path(),m);
                                        }    
                                    }
                                    else if (meth.isAnnotationPresent(Post.class)) {
                                        if(hm.containsKey(getAnnotation.path())){
                                        
                                            hm.get(getAnnotation.path()).getVerbmethods().add(new VerbMethod(meth,"Post"));
                                        
                                        }
                                        else{
                                            Mapping m=new Mapping(clazz.getName());
                                            m.getVerbmethods().add(new VerbMethod(meth,"Post"));
                                            hm.put(getAnnotation.path(),m);
                                        } 
                                    }
                                    

                                    
                                   
                                }
                            }
                        }
                    }
                }
            }
            else {
                throw new Exception("empty package");
            }
        
    }
  

  public static boolean checkRestMethod(Method method,Class<RestApi> annotationClass)
        {

                if (method.isAnnotationPresent(annotationClass)) {
                    return true;
                }
                return false;
        }

    // public Object[] getArgs(Map<String, String[]> params, Method method) throws Exception {
    //     List<Object> ls = new ArrayList<Object>();
    //     for (Parameter param : method.getParameters()) {
    //         String key = null;
    //         System.out.println(param.getName());
    //         if (params.containsKey(param.getName())) {
    //             key = param.getName();
    //         } else if (param.isAnnotationPresent(Param.class)
    //                 && params.containsKey(param.getAnnotation(Param.class).name())) {
    //             key = param.getAnnotation(Param.class).name();
    //         }
    //         /// Traitement type
    //         Class<?> typage = param.getType();
    //         /// Traitement values
           
    //         if (params.get(key).length == 1) {
    //             ls.add(this.parse(params.get(key)[0],typage));
    //         } 
    //         else if (params.get(key).length > 1) {
    //             ls.add(this.parse(params.get(key),typage));
    //         } 
    //         else if (params.get(key) == null) {
    //             ls.add(null);
    //         }
    //     }
    //     return ls.toArray();
    // }

    public Object[] getArgs(Map<String, String[]> params, Method method,HttpSession session,Part part) throws Exception {
        List<Object> ls = new ArrayList<Object>();
        for (Parameter param : method.getParameters()) {
            String key = null;
            /// Traitement type
            Class<?> typage = param.getType();
           
            if (!param.getType().isPrimitive() && !param.getType().equals(String.class)) {
                Class<?> c=param.getType();
                if (c.equals(MySession.class)) {
                    ls.add( new MySession(session));
                }
                else {
                    String nomObjet=null;
                if(param.isAnnotationPresent(ObjectParam.class)){
                    nomObjet=c.getAnnotation(ObjectParam.class).name();
                }
                else{
                    nomObjet=param.getName();
                }
                Object o=c.getConstructor((Class[])null).newInstance((Object[])null);
                ///prendre les attributs
                Field[] f=c.getDeclaredFields();
                for (Field field : f) {
                    System.out.println(nomObjet+"."+field.getName());
                        if (params.containsKey(nomObjet+"."+field.getName())) {
                            System.out.println(params.containsKey(nomObjet+"."+field.getName()));
                            String fieldName=field.getName().substring(0, 1).toUpperCase() +field.getName().substring(1);
                            Method toInvoke=c.getDeclaredMethod("set"+fieldName,field.getType());
                            toInvoke.invoke(o,this.parse(params.get(nomObjet+"."+field.getName())[0],field.getType()));
                        }
                }
                validate(o);

                ls.add(o);
                }
                
            } else {
                if (params.containsKey(param.getName())) {
                    key = param.getName();
                }
                
                
                else if (param.isAnnotationPresent(Param.class) && params.containsKey(param.getAnnotation(Param.class).name())) {
                    
                
                    key = param.getAnnotation(Param.class).name();
                }
                /// Traitement values
                 if (params.get(key) == null) {
                    ls.add(null);
                }else if (params.get(key).length == 1) {
                    ls.add(this.parse(params.get(key)[0], typage));
                } else if (params.get(key).length > 1) {
                    ls.add(this.parse(params.get(key), typage));
                }
                else if (part != null) {
                    Fichier fichier = new Fichier(part);
                    ls.add(fichier);
                } 
            }

        }
        return ls.toArray();
    }   

    public static void validate(Object object) throws ValidationException {
        if (object == null)
            throw new ValidationException("L'objet à valider ne peut pas être nul.");

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                validateField(field, value);
            } catch (IllegalAccessException e) {
                throw new ValidationException("Erreur d'accès au champ : " + field.getName(), e);
            }
        }
    }

    private static void validateField(Field field, Object value) throws ValidationException {
        if (field.isAnnotationPresent(NotNull.class) && value == null) {
            throw new ValidationException(field.getAnnotation(NotNull.class).message());
        }
        if (field.isAnnotationPresent(Min.class) && value instanceof Number) {
            long minValue = field.getAnnotation(Min.class).value();
            if (((Number) value).longValue() < minValue) {
                throw new ValidationException(
                        field.getAnnotation(Min.class).message().replace("{value}", String.valueOf(minValue)));
            }
        }
        if (field.isAnnotationPresent(Max.class) && value instanceof Number) {
            long maxValue = field.getAnnotation(Max.class).value();
            if (((Number) value).longValue() > maxValue) {
                throw new ValidationException(
                        field.getAnnotation(Max.class).message().replace("{value}", String.valueOf(maxValue)));
            }
        }
        if (field.isAnnotationPresent(Size.class) && value instanceof String) {
            Size annotation = field.getAnnotation(Size.class);
            int length = ((String) value).length();
            if (length < annotation.min() || length > annotation.max()) {
                throw new ValidationException(annotation.message().replace("{min}", String.valueOf(annotation.min()))
                        .replace("{max}", String.valueOf(annotation.max())));
            }
        }
    }
    
}
