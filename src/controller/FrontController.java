
package controller;
import java.io.*;
import java.net.URLDecoder;
import java.text.Annotation;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.List;
import outils.*;
/**
 * FrontController
 */
public class FrontController extends HttpServlet {

        private List<String> listController;
         private boolean initiated=false;

      

        protected void processRequest(HttpServletRequest req,HttpServletResponse res)
         throws IOException {
            PrintWriter out=res.getWriter();
            StringBuffer url= req.getRequestURL();
            String packageToScan = this.getInitParameter("package_name");
            out.println("<!DOCTYPE html>");
            out.println("<head>");
            out.println("<title>");
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
        try {
            if (!initiated) {
                listController= new ControllerUtils().getAllAnnoted(packageToScan,Controller.class);
                this.initiated=true;
                out.println("Premier  et dernier scan");
            }    
        } catch (Exception e) {
            
        }
        


        for (String class1 : listController) {
                out.println("<h2> ito le zavatra "+class1+"</h2>");

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