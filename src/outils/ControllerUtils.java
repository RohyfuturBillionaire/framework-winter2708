package outils;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ControllerUtils {

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
        try {
            
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
                                    hm.put(getAnnotation.url(),new Mapping(clazz.getSimpleName(),meth.getName()));
                                }
                            }
                        }
                    }
                }
            }
           
        } catch (Exception e) {
            throw e;
        }
        return hm;
    }
    
}
