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
import java.util.Map;

public class ExpediaVisitHistory extends HttpServlet {

    /**
     * Calls this method on get Request: "/expediahistory"
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();

            String sessionUsername = SessionHandler.getSession(request, "username");
            String lastLoginDate = SessionHandler.getSession(request, "lastLoginDate");
            if (sessionUsername == null) {
                response.sendRedirect("/login");
            } else {
                VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();

                DatabaseHandler handler = DatabaseHandler.getInstance();
                Map<String, String> expediaLinkByHotelId = handler.getUserExpediaVisitHistory(sessionUsername);


                Template template = ve.getTemplate("templates/expediavisithistory.html");

                //set context
                if (expediaLinkByHotelId == null) {
                    context.put("expediaLinkByHotelId", "");
                } else {
                    context.put("expediaLinkByHotelId", expediaLinkByHotelId);
                }
                context.put("lastLoginDate", lastLoginDate.trim());

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
