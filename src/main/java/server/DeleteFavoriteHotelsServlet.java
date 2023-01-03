package server;

import commmonFunctions.SessionHandler;
import dboperations.DatabaseHandler;
import dboperations.HotelsDatabaseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteFavoriteHotelsServlet extends HttpServlet {

    /**
     * Delete Favorite Hotels.(Used for Ajax calls.)
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();

            String sessionUsername = SessionHandler.getSession(request, "username");

            if (sessionUsername == null) {
                response.sendRedirect("/login");
            } else {
                String hotelName = request.getParameter("hotelName");
                HotelsDatabaseHandler handler = HotelsDatabaseHandler.getInstance();
                if (hotelName == null) {
                    handler.deleteAllFavoriteHotels(sessionUsername);
                } else {
                    handler.deleteFavoriteHotels(sessionUsername, hotelName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
