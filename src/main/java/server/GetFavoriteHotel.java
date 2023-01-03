package server;

import commmonFunctions.SessionHandler;
import dboperations.HotelsDatabaseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetFavoriteHotel extends HttpServlet {

    /**
     * Returns if the hotelId passed as paramter is a favorite hotel of the user.(Used for AJAX Calls)
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

                HotelsDatabaseHandler handler = HotelsDatabaseHandler.getInstance();
                Boolean isFavorite = handler.getFavoriteHotel(hotelId, sessionUsername);
                out.println(isFavorite);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
