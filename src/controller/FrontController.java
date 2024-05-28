
package controller;
import java.io.*;
import java.net.URLDecoder;
import java.text.Annotation;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import outils.*;
/**
 * FrontController
 */
public class FrontController extends HttpServlet {
         HashMap<String,Mapping> map;
         public void init() throws ServletException {
                 try {

                     String package_name = this.getInitParameter("package_name");
                     map =ControllerUtils.getAllClassesSelonAnnotation(package_name,Controller.class);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
         
             }

        // public void init() throws ServletException{
        //         try {
        //                 getController();
        //         } catch (Exception e) {
        //                 e.getStackTrace();
        //         }
        // }

        protected void processRequest(HttpServletRequest req,HttpServletResponse res)
         throws IOException {
            PrintWriter out=res.getWriter();
            StringBuffer url= req.getRequestURL();
            Boolean ifUrlExist = false;
            String packageToScan = this.getInitParameter("package_name");
            out.println("<!DOCTYPE html>");
            out.println("<head>");
            out.println("<title>");
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
        try {

            
        for (String cle : map.keySet()) {
            if(cle.equals(req.getRequestURI().toString())){
                out.println("Votre url : "+url +" est associe a la methode : "+ map.get(cle).getMethodeName()+" dans la classe : "+(map.get(cle).getClassName()));
                ifUrlExist = true;
            }
        }
            if (!ifUrlExist) {
                out.println("Aucune methode n'est associe a l url : "+url);
            }
        } catch (Exception e) {
            
        }
        
            out.println("<h2> ito le zavatra "+url+"</h2>");
            out.println("</body>");
            out.println("</html>");
        
        }

        protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
                processRequest(request, response);
        
        }

      
        

        protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
                
            processRequest(request, response);
            
        }

    
}