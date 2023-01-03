package server;

import commmonFunctions.SessionHandler;
import dboperations.HotelsDatabaseHandler;
import hotelapp.Hotel;
import hotelapp.ThreadSafeHotelCollection;
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
import java.util.List;
import java.util.Set;

public class HotelSearchServlet extends HttpServlet {
    List<Hotel> hotels;

    /**
     * Calls this method on Get request: "/hotelsearch".
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
                VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();

                Template template = ve.getTemplate("templates/hotelsearch.html");
                if (hotels != null) {
                    context.put("hotels", hotels);
                } else {
                    context.put("hotels", "");
                }
                String postServletName = request.getServletPath();
                context.put("postServlet", postServletName);
                context.put("lastLoginDate", lastLoginDate.trim());

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls this method on Post request: "/hotelsearch".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String keyword = request.getParameter("keyword");
            keyword = StringEscapeUtils.escapeHtml4(keyword);

            HotelsDatabaseHandler handler = HotelsDatabaseHandler.getInstance();
            hotels = handler.getHotels(keyword);

            response.sendRedirect("/hotelsearch");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
