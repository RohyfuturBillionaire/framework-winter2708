
package controller;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


/**
 * FrontController
 */
public class FrontController extends HttpServlet {

        protected void processRequest(HttpServletRequest req,HttpServletResponse res)
         throws IOException {
            PrintWriter out=res.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<head>");
            out.println("<title>");
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2> ito le zavatra "+req.getRequestURL()+"</h2>");
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