package server;

import dboperations.DatabaseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddToExpediaHistoryTableServlet extends HttpServlet {
    /**
     * Inserts Expedia Link history to table.
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String hotelId = request.getParameter("hotelId");
        String username = request.getParameter("username");
        String expediaLink = request.getParameter("expedialink");
        DatabaseHandler handler = DatabaseHandler.getInstance();
        handler.insertInExpediaHistoryTable(hotelId, username, expediaLink);
    }
}
