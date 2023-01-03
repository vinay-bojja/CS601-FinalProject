package server;

import commmonFunctions.SessionHandler;
import dboperations.DatabaseHandler;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class HomepageServlet extends HttpServlet {

    /**
     * Calls this method on Get request:"/homepage".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String username = SessionHandler.getSession(request, "username");
            String lastLoginDate = SessionHandler.getSession(request, "lastLoginDate");
            if (username == null) {
                response.sendRedirect("/login");
            } else {
                VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();

                Template template = ve.getTemplate("templates/homepage.html");
                context.put("username", username);
                context.put("lastLoginDate", lastLoginDate.trim());
                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
