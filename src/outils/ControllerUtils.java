package outils;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static HashMap getAllClassesSelonAnnotation(String packageToScan,Class<?>annotation) throws Exception{
        //List<String> controllerNames = new ArrayList<>();
        HashMap<String,Mapping> hm=new HashMap<>();
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
                                if (meth.isAnnotationPresent(Get.class)) {
                                    Get getAnnotation= meth.getAnnotation(Get.class);
                                    if(hm.containsKey(getAnnotation.url())){
                                        throw new Exception("dupicate method annotation"+getAnnotation.url());
                                    }
                                    
                                    hm.put(getAnnotation.url(),new Mapping(clazz.getName(),meth.getName()));
                                }
                            }
                        }
                    }
                }
            }
            else {
                throw new Exception("empty package");
            }
        return hm;
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

    public Object[] getArgs(Map<String, String[]> params, Method method) throws Exception {
        List<Object> ls = new ArrayList<Object>();
        for (Parameter param : method.getParameters()) {
            String key = null;
            /// Traitement type
            Class<?> typage = param.getType();
            if (!param.getType().isPrimitive() && !param.getType().equals(String.class)) {
                Class<?> c=param.getType();
                String nomObjet=null;
                if(c.isAnnotationPresent(ObjectParam.class)){
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
                ls.add(o);
            } else {
                if (params.containsKey(param.getName())) {
                    key = param.getName();
                } else if (param.isAnnotationPresent(Param.class)
                        && params.containsKey(param.getAnnotation(Param.class).name())) {
                    key = param.getAnnotation(Param.class).name();
                }
                /// Traitement values
                if (params.get(key).length == 1) {
                    ls.add(this.parse(params.get(key)[0], typage));
                } else if (params.get(key).length > 1) {
                    ls.add(this.parse(params.get(key), typage));
                } else if (params.get(key) == null) {
                    ls.add(null);
                }
            }

        }
        return ls.toArray();
    }   
    
}
