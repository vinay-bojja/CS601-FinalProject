package server;

import commmonFunctions.SessionHandler;
import dboperations.HotelsDatabaseHandler;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetUserAllFavoriteHotels extends HttpServlet {

    /**
     * Return all favorite Hotels of user in Json format.
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
            if (username == null) {
                response.sendRedirect("/login");
            } else {
                VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();

                HotelsDatabaseHandler handler = HotelsDatabaseHandler.getInstance();
                String allFavoriteHotelsJSON = handler.getAllFavoriteHotels(username);
                out.println(allFavoriteHotelsJSON);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
