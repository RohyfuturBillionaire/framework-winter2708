
package controller;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import com.google.gson.Gson;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import outils.*;
@MultipartConfig
public class FrontController extends HttpServlet {
    HashMap<String, Mapping> map = new HashMap<>();

    public void init() throws ServletException {
        try {
            String package_name = this.getInitParameter("package_name");
            ControllerUtils.getAllClassesSelonAnnotation(package_name, Controller.class, map);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        PrintWriter out = res.getWriter();
        StringBuffer url = req.getRequestURL();
        System.out.println("here is the url :"+url);
        Boolean ifUrlExist = false;
        Object toPrint = null;
        String urL = new ControllerUtils().getURIwithoutContextPath(req);
        System.out.println("here is the urL :"+urL);
        if (urL.contains("?")) {
            System.out.println("ato izy");
            urL = urL.split("?")[0];
            System.out.println( "uuuuu" +urL);
        }

        ControllerUtils cont = new ControllerUtils();
        try {

            for (String cle : map.keySet()) {
                System.out.println("here is the cle :" +cle);
                if (cle.equals(urL)) {
                    Set<VerbMethod> verbMethods = map.get(cle).getVerbmethods();
                    Class<?> clas = Class.forName(map.get(cle).getClassName());
                    Object caller = ControllerUtils.checkSession(clas, req.getSession());
                    Map<String, String[]> parameters = req.getParameterMap();

                    for (String cles  : parameters.keySet()) {
                        System.out.println("cles de :" + cles);
                        
                        for (String para : parameters.get(cles)) {
                            System.out.println("parameters"+ para);
                        }
                    }

                    Method iray = null;

                    Method[] thod = clas.getDeclaredMethods();
                    for (Method method : thod) {
                        
                        for (VerbMethod verbMethod : verbMethods) {
                            if (method.getName().equals(verbMethod.getMethod().getName())) {
                                iray = method;
                                break;
                            }
                        }
                        if (iray != null) {
                            break;
                        }
                    }
                    if (parameters != null) {
                        if (cont.checkSessionNeed(iray)) {
                            
                            toPrint = iray.invoke(caller, cont.getArgs(req,parameters, iray,req.getSession()));
                        
                        } else {
                            toPrint = iray.invoke(caller, cont.getArgs(req,parameters, iray, null));
                        }
                    } 
                        toPrint = iray.invoke(caller, (Object[]) null);
                    

                    if (ControllerUtils.checkRestMethod(iray, RestApi.class)) {
                        Gson json = new Gson();
                        if (toPrint instanceof String) {

                            String restJson = json.toJson(toPrint, String.class);
                            out.print(restJson);
                        } else if (toPrint instanceof ModelView) {
                            ModelView model = (ModelView) toPrint;
                             String view=model.getUrl();

                            RequestDispatcher dispat = req.getRequestDispatcher(view);
                            HashMap<String, Object> modelObjects = null;
                            if (model.getData() != null) {
                                modelObjects = model.getData();
                                for (String nomdata : modelObjects.keySet()) {
                                req.setAttribute(nomdata,modelObjects.get(nomdata));
                                }
                                // out.print(json.toJson(modelObjects, HashMap.class));
                            }

                             dispat.forward(req,res);
                        } else {
                            throw new Exception("invalid type");
                        }

                    }

                    else {

                        if (toPrint instanceof String) {
                            out.print(toPrint);
                        } else if (toPrint instanceof ModelView) {
                            ModelView model = (ModelView) toPrint;
                            String view = model.getUrl();
                       
                            
                            System.out.println("here is the view context :"+new ControllerUtils().getBaseUrl(req));
                          
                            RequestDispatcher dispat = req.getRequestDispatcher("/"+view);
                            HashMap<String, Object> modelObjects = null;
                            if (model.getData() != null) {
                                modelObjects = model.getData();
                                for (String nomdata : modelObjects.keySet()) {
                                    req.setAttribute(nomdata, modelObjects.get(nomdata));
                                }
                            }
                            

                            dispat.forward(req, res);
                        } else {
                            throw new Exception("invalid type");
                        }
                    }

                    ifUrlExist = true;
                    break;
                }
            }
            if (!ifUrlExist) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND); // Sets the status code to 404
            res.setContentType("text/html");
            out.print("<!DOCTYPE html>");
            out.print("<html lang=\"en\">");
            out.print("<head>");

            out.print("<meta charset=\"UTF-8\">");
            out.print("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.print("<title>404 - Page Not Found</title>");
            out.print("<style>");
            out.print("body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }");
            out.print("h1 { font-size: 50px; color: #FF6347; }");
            out.print("p { font-size: 20px; color: #333; }");
            out.print("</style>");
            out.print("</head>");
            out.print("<body>");
            out.print("<h1>404</h1>");
            out.print("<p>Oops! The page you are looking for does not exist.</p>");
            out.print("<p>Please check the URL or go back to the homepage.</p>");
            out.print("</body>");
            out.print("</html>");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

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