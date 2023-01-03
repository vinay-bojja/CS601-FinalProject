package server;

import commmonFunctions.SessionHandler;
import commmonFunctions.Validations;
import dboperations.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class UserRegistrationServlet extends HttpServlet {
    /**
     * Calls this methods on Get request: "/register-user".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();

            String sessionUsername = SessionHandler.getSession(request, "username");
            if (sessionUsername != null) {
                response.sendRedirect("/homepage");
            } else {
                VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();
                String errorMessage = request.getParameter("errormessage");

                //Call html file
                Template template = ve.getTemplate("templates/registration.html");

                //set values
                String doPostServletName = request.getServletPath();
                context.put("doPostServlet", doPostServletName);
                if (errorMessage != null) {
                    context.put("errorMessage", errorMessage);
                } else {
                    context.put("errorMessage", "");
                }

                //merge into template
                StringWriter writer = new StringWriter();
                template.merge(context, writer);

                out.println(writer.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls this method on Post request: "/register-user".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            DatabaseHandler dbHandler = DatabaseHandler.getInstance();
            String username = request.getParameter("username");
            username = StringEscapeUtils.escapeHtml4(username);
            String password = request.getParameter("password");
            password = StringEscapeUtils.escapeHtml4(password);
            String confirmPassword = request.getParameter("confirmpassword");
            confirmPassword = StringEscapeUtils.escapeHtml4(confirmPassword);
            Boolean isPasswordAndConfirmPasswordSame = Validations.isPasswordAndConfirmPasswordSame(password, confirmPassword);
            Boolean doesPasswordMatchRegex = Validations.doesPasswordMatchRegex(password, confirmPassword);
            if (!doesPasswordMatchRegex) {
                response.sendRedirect("/register-user?errormessage=" + "Password doesn't match the required format.");
            } else if (isPasswordAndConfirmPasswordSame) {
                dbHandler.createTable();
                Map<Boolean, String> result = dbHandler.registerUser(username, password);
                if (result.containsKey(true)) {
                    response.sendRedirect("/login?registered=1");
                } else {
                    response.sendRedirect("/register-user?errormessage=" + result.get(false));
                }
            } else {
                response.sendRedirect("/register-user?errormessage=" + "Password and Confirm password should be same.");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
