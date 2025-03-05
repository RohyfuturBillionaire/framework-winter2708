package outils;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import outils.ValidationAnnotation.Max;
import outils.ValidationAnnotation.Min;
import outils.ValidationAnnotation.NotEmpty;
import outils.ValidationAnnotation.NotNull;
import outils.ValidationAnnotation.Numeric;
import outils.ValidationAnnotation.Size;
import outils.ValidationException;
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

    
    public String getURIwithoutContextPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
        
    }

    public String getBaseUrl(HttpServletRequest req){
        String scheme = req.getScheme(); 
        String serverName = req.getServerName(); // localhost
                            int serverPort = req.getServerPort(); // 8080
                            String contextPath = req.getContextPath(); // /mywebapp
                            String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath + "/";
                            return baseURL;
    }

    public static void getAllClassesSelonAnnotation(String packageToScan,Class<?>annotation,HashMap<String,Mapping> map) throws Exception{
        //List<String> controllerNames = new ArrayList<>();
        HashMap<String,Mapping> hm=map;
         //String path = getClass().getClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String path = Thread.currentThread().getContextClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File packageDir = new File(decodedPath);
            System.out.println("package Dir" + packageDir);

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
    public Object[] getArgs(HttpServletRequest req,Map<String, String[]> params, Method method,HttpSession session) throws Exception {
        List<Object> ls = new ArrayList<Object>();
        
        for (Parameter param : method.getParameters()) {
            String key = null;
            /// Traitement type
            Class<?> typage = param.getType();
            System.out.println("param " + param.getType());
            
            if (!param.getType().isPrimitive() && !param.getType().equals(String.class)) {
                Class<?> c=param.getType();
                if (c.equals(MySession.class)) {
                    ls.add( new MySession(session));
                  }else if (c.equals(Fichier.class)) {
                   
                    if (param.isAnnotationPresent(Param.class)) {
                        key = param.getAnnotation(Param.class).name();
                    
                    } else {
                        key = param.getName();
                    }
                    
                    System.out.println("key" + key);
                    ls.add(new Fichier(req.getPart(key)));
                } else {
                    String nomObjet=null;
                        if(param.isAnnotationPresent(ObjectParam.class)){
                            nomObjet=c.getAnnotation(ObjectParam.class).name();
                        }
                        else{
                            nomObjet=param.getName();
                        }
                Object o=c.getConstructor((Class[])null).newInstance((Object[])null);
                ///prendre les attributs
                validate(o,params,nomObjet);
                ls.add(o);
                }
                
            } else {
                System.out.println("test de dernier minute");
                if (params.containsKey(param.getName())) {
                    key = param.getName();
                } else if (param.isAnnotationPresent(Param.class) && params.containsKey(param.getAnnotation(Param.class).name())) {
                    key = param.getAnnotation(Param.class).name();
                }
                /// Traitement values
                if (params.get(key) == null) {
                    ls.add(null);
                } else if (params.get(key).length == 1) {
                    ls.add(this.parse(params.get(key)[0], typage));
                } else if (params.get(key).length > 1) {
                    ls.add(this.parse(params.get(key), typage));
                } 
            }

        }
        return ls.toArray();

    }   

    public  void validate(Object object,Map<String, String[]> params,String nomObjet) throws Exception {
        if (object == null)
            throw new ValidationException("L'objet à valider ne peut pas être nul.");

        Class<?> c=object.getClass();
        System.out.println("objet nature"+object);
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            if (params.containsKey(nomObjet+"."+field.getName())) {
                System.out.println(params.containsKey(nomObjet+"."+field.getName()));
                String fieldName=field.getName().substring(0, 1).toUpperCase() +field.getName().substring(1);
                Method toInvoke=c.getDeclaredMethod("set"+fieldName,field.getType());
                try {
                    Object value = params.get(nomObjet+"."+field.getName())[0];
                    System.out.println("nature de la valeur"+ value);
                    validateField(field, value);
                    // toInvoke.invoke(object, this.parse(value,field.getType()));
    
                } catch (Exception e) {
                    throw new ValidationException(e.getMessage());
                }    
            }
            
        }
    }

    private void validateField(Field field, Object value) throws ValidationException {
        System.out.println("goo");
        for (Annotation iterable_element : field.getAnnotations()) {
            System.out.println("annotation nature :" + iterable_element );    
        }
        
        if (field.isAnnotationPresent(NotNull.class) && value == null) 
        
        {throw new ValidationException(field.getAnnotation(NotNull.class).message());}

        if (field.isAnnotationPresent(NotEmpty.class) && value=="") 
        
        {throw new ValidationException(field.getAnnotation(NotEmpty.class).message());}

        if(field.isAnnotationPresent(Numeric.class))
            {   String ann=field.getAnnotation(Numeric.class).message();
                System.out.println("annotation ty"+ ann);
                try {
                    this.parse(value,field.getType());
                    } catch (Exception e) {
                     throw new ValidationException(ann);
                }
                
            }

        if (field.isAnnotationPresent(Min.class)) {
            long minValue = field.getAnnotation(Min.class).value();
            if ( (double)this.parse(value, field.getType()) < minValue) {
                throw new ValidationException(
                        field.getAnnotation(Min.class).message().replace("{value}", String.valueOf(minValue)));
            }
        }
        if (field.isAnnotationPresent(Max.class)) {
            long maxValue = field.getAnnotation(Max.class).value();
            if ((double) this.parse(value, field.getType()) > maxValue) {
                throw new ValidationException(
                        field.getAnnotation(Max.class).message().replace("{value}", String.valueOf(maxValue)));
            }
        }
        // if (field.isAnnotationPresent(Size.class) && value instanceof String) {
        //     Size annotation = field.getAnnotation(Size.class);
        //     int length = ((String) value).length();
        //     if (length < annotation.min() || length > annotation.max()) {
        //         throw new ValidationException(annotation.message().replace("{min}", String.valueOf(annotation.min()))
        //                 .replace("{max}", String.valueOf(annotation.max())));
        //     }
        // }
    }
    
}
