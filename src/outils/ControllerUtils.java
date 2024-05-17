package outils;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
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
    
}
