
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
        ValueController errrorsValue= null;
        String urL = new ControllerUtils().getURIwithoutContextPath(req);
        String urlError=null;
        boolean unAutorized=false;
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
                    User user=(User)req.getSession().getAttribute("user");
                    
                     if (clas.isAnnotationPresent(Auth.class)) {
                        if (user==null) {
                            unAutorized=true;
                            break;
                        }
                        else if (!user.getRole().equals(clas.getAnnotation(Auth.class).role())) {
                            unAutorized=true;
                            break;
                        }
                    }


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
                    if (iray.isAnnotationPresent(Auth.class)) {
                        if (user==null) {
                            unAutorized=true;
                            break;
                        }
                        else if (!user.getRole().equals(iray.getAnnotation(Auth.class).role())) {
                            unAutorized=true;
                            break;
                        }
                    }
                    if (parameters != null) {
                          Object[] objects=null;
                        if (cont.checkSessionNeed(iray)) {
                          
                            try {
                              objects=cont.getArgs(req,parameters, iray,req.getSession());    
                            } catch (ValidationException e) {
                                errrorsValue=e.getValueController();
                                urlError=e.getUrl();
                                
                            }
                            
                            
                            toPrint = iray.invoke(caller,objects);
                        
                        } else {
                            try {
                                objects=cont.getArgs(req,parameters, iray,req.getSession());    
                                toPrint = iray.invoke(caller,objects);
                            } catch (ValidationException e) {
                               errrorsValue=e.getValueController();
                               urlError=e.getUrl();
                           }
                        
                           
                        }
                    } 

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

                        if (errrorsValue!=null) {
                            // String uri = req.getRequestURI();
                            System.out.println("error url :"+urlError);
                            
                            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(req) {
                                @Override
                                public String getMethod() {
                                    return "GET";
                                }
                            };
            
                            wrappedRequest.setAttribute("errors", errrorsValue);
                            // Dispatch the new request to errorUrl
                            RequestDispatcher dispatcher = wrappedRequest.getRequestDispatcher(urlError);
                            dispatcher.forward(wrappedRequest,res);
                        } else {

                            if (toPrint instanceof String) {
                                out.print(toPrint);
                            } else if (toPrint instanceof ModelView) {
                                ModelView model = (ModelView) toPrint;
                                String view = model.getUrl();
                                 // http
                                RequestDispatcher dispat =null;
                                System.out.println("here is the view context :"+new ControllerUtils().getBaseUrl(req));
                                dispat = req.getRequestDispatcher("/"+view);     
                                
                                
    
                               
                                HashMap<String, Object> modelObjects = null;
                                if (model.getData() != null) {
                                    modelObjects = model.getData();
                                    for (String nomdata : modelObjects.keySet()) {
                                        req.setAttribute(nomdata, modelObjects.get(nomdata));
                                    }
                                }
                                
                                // if (view.contains("redirect:")) {
                                //     String[] split = view.split(":");
                                //     res.sendRedirect(split[1]);
                                    
                                // }
                                dispat.forward(req, res);
                            } else {
                                throw new Exception("invalid type");
                            }

                        }

                        
                    }

                    ifUrlExist = true;
                    break;
                }
            }

            if (unAutorized) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN); // Sets the status code to 404
                res.setContentType("text/html");
                out.print("<!DOCTYPE html>");
                out.print("<html lang=\"en\">");
                out.print("<head>");
    
                out.print("<meta charset=\"UTF-8\">");
                out.print("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                out.print("<title>403 - Access denied</title>");
                out.print("<style>");
                out.print("body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }");
                out.print("h1 { font-size: 50px; color: #FF6347; }");
                out.print("p { font-size: 20px; color: #333; }");
                out.print("</style>");
                out.print("</head>");
                out.print("<body>");
                out.print("<h1>403</h1>");
                out.print("<p>Oops! you dont have access to this page sorry.</p>");
                out.print("<p>Please go back to the homepage.</p>");
                out.print("</body>");
                out.print("</html>");
                    
                }
            
            
            else if (!ifUrlExist) {
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