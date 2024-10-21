
package controller;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.Annotation;
import java.util.*;

import com.google.gson.Gson;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import outils.*;

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
        Boolean ifUrlExist = false;
        Object toPrint = null;
        String urL = req.getRequestURI().toString();
        if (urL.contains("?")) {
            urL = urL.split("?")[0];
        }

        ControllerUtils cont = new ControllerUtils();
        try {
            for (String cle : map.keySet()) {
                if (cle.equals(urL)) {
                    Class<?> clas = Class.forName(map.get(cle).getClassName());
                    Object caller = ControllerUtils.checkSession(clas, req.getSession());
                    Map<String, String[]> parameters = req.getParameterMap();
                    Method iray = null;
                    if (parameters != null) {
                        Method[] thod = clas.getDeclaredMethods();
                        for (Method method : thod) {
                            if (method.getName() == map.get(cle).getMethod()) {
                                iray = method;
                                break;
                            }
                        }
                        if (cont.checkSessionNeed(iray)) {
                            toPrint = iray.invoke(caller, cont.getArgs(parameters, iray, req.getSession()));
                        } else {
                            toPrint = iray.invoke(caller, cont.getArgs(parameters, iray, null));
                        }
                    } else {
                        iray = clas.getDeclaredMethod(map.get(cle).getMethodeName(), (Class<?>[]) null);
                        toPrint = iray.invoke(caller, (Object[]) null);
                    }

                    if (ControllerUtils.checkRestMethod(iray, RestApi.class)) {
                        Gson json = new Gson();
                        if (toPrint instanceof String) {

                            String restJson = json.toJson(toPrint, String.class);
                            out.print(restJson);
                        } else if (toPrint instanceof ModelView) {
                            ModelView model = (ModelView) toPrint;
                            // String view=model.getUrl();

                            // RequestDispatcher dispat = req.getRequestDispatcher(view);
                            HashMap<String, Object> modelObjects = null;
                            if (model.getData() != null) {
                                modelObjects = model.getData();
                                // for (String nomdata : modelObjects.keySet()) {
                                // req.setAttribute(nomdata,modelObjects.get(nomdata));
                                // }
                                out.print(json.toJson(modelObjects, HashMap.class));
                            }

                            // dispat.forward(req,res);
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
                            RequestDispatcher dispat = req.getRequestDispatcher(view);
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
                throw new Exception("Aucune methode n'est associe a l url : " + url);
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