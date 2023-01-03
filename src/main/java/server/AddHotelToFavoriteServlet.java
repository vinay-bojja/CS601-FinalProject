package server;

import commmonFunctions.SessionHandler;
import dboperations.HotelsDatabaseHandler;
import dboperations.ReviewsDatabaseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AddHotelToFavoriteServlet extends HttpServlet {
    /**
     * Adds Hotel to Favorites(AJAX calls)
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
                String hotelId = request.getParameter("hotelId");
                String hotelName = request.getParameter("hotelName");

                HotelsDatabaseHandler handler = HotelsDatabaseHandler.getInstance();
                handler.addHotelToFavorite(hotelId, hotelName, sessionUsername);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
