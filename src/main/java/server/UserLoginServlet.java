package server;

import commmonFunctions.SessionHandler;
import commmonFunctions.Validations;
import dboperations.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserLoginServlet extends HttpServlet {
    /**
     * Calls this method on Get request: "/login".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            //Session
            String sessionUsername = SessionHandler.getSession(request, "username");
            if (sessionUsername != null) {
                response.sendRedirect("/homepage");
            } else {

                VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();

                //get parameters
                String registered = request.getParameter("registered");
                registered = StringEscapeUtils.escapeHtml4(registered);
                String didsignout = request.getParameter("didsignout");
                didsignout = StringEscapeUtils.escapeHtml4(didsignout);
                String error = request.getParameter("error");

                //Call html file
                Template template = ve.getTemplate("templates/login.html");

                String doPostServletName = request.getServletPath();
                context.put("doPostServlet", doPostServletName);
                if (registered != null) {
                    context.put("afterRegistrationMessage", "Registration successful. Please login.");
                } else {
                    context.put("afterRegistrationMessage", "");
                }
                if (didsignout != null) {
                    context.put("signoutMessage", "Signed out successfully.");
                } else {
                    context.put("signoutMessage", "");
                }
                if (error == null) {
                    context.put("errormessage", "");
                } else {
                    context.put("errormessage", "Username or Password invalid.");
                }

                //merge into template
                StringWriter writer = new StringWriter();
                template.merge(context, writer);

                out.println(writer);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Calls this methods on Post Request: "/login".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String user = request.getParameter("username");
            user = StringEscapeUtils.escapeHtml4(user);
            String pass = request.getParameter("password");
            pass = StringEscapeUtils.escapeHtml4(pass);

            DatabaseHandler dbHandler = DatabaseHandler.getInstance();


            boolean flag = dbHandler.authenticateUser(user, pass);
            if (flag) {
                SessionHandler.setSession(request, "username", user);
                //set context values
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                SessionHandler.setSession(request, "loginDateTime", dtf.format(now));

                DatabaseHandler handler = DatabaseHandler.getInstance();
                String lastLoginDate = handler.getLoginDateTime(user);
                SessionHandler.setSession(request, "lastLoginDate", lastLoginDate.trim());

                response.sendRedirect("/homepage");
            } else {
                response.sendRedirect("/login?error=1");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
