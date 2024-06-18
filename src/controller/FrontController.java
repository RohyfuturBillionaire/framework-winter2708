
package controller;
import java.io.*;
import java.lang.reflect.Method;
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
                    throw new ServletException(e);
                 }
        }

        // public void init() throws ServletException{
        //         try {
        //                 getController();
        //         } catch (Exception e) {
        //                 e.getStackTrace();
        //         }
        // }

        protected void processRequest(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
            PrintWriter out=res.getWriter();
            StringBuffer url= req.getRequestURL();
            Boolean ifUrlExist = false;
            String packageToScan=this.getInitParameter("package_name");
            Object toPrint=null;
            try {
            for (String cle : map.keySet()) {
                if(cle.equals(req.getRequestURI().toString())){
                    Class<?>clas=Class.forName(map.get(cle).getClassName());
                    Map<String,String []> parameters =req.getParameters();
                    Method iray=null;
                    if (parameters!=null) {
                        iray=clas.getDeclaredMethod(map.get(cle).getMethodeName(),(Class<?>[])null);
                    }
                    else{
                        iray=clas.getDeclaredMethod(map.get(cle).getMethodeName(),(Class<?>[])null);
                    }
                    Object caller=clas.getDeclaredConstructor().newInstance((Object[])null);
                    toPrint=iray.invoke(caller,(Object[])null);
                if (toPrint instanceof String ) {
                    out.print(toPrint);
                 } else if (toPrint instanceof ModelView) {
                    ModelView model=(ModelView)toPrint;
                    String view=model.getUrl();
                    RequestDispatcher dispat = req.getRequestDispatcher(view);
                    HashMap <String , Object> modelObjects=model.getData();
                    for (String nomdata : modelObjects.keySet()) {
                        req.setAttribute(nomdata,modelObjects.get(nomdata));    
                    }
                    dispat.forward(req,res);
                } else { throw new Exception("invalid type"); }
                ifUrlExist = true;
                break;
            }
        } if (!ifUrlExist) {
                throw new Exception("Aucune methode n'est associe a l url : "+url);
            }
        } catch (Exception e) {
           throw new ServletException(e);
        }

    }

        protected void doGet(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException { processRequest(request,response); }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException { 
            processRequest(request, response); 
        }

    
}