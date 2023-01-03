package server;

import commmonFunctions.SessionHandler;
import dboperations.DatabaseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteExpediaVisitHistory extends HttpServlet {

    /**
     * Deletes Expedia visit history.
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
                String id = request.getParameter("id");
                DatabaseHandler handler = DatabaseHandler.getInstance();
                if (id == null) {
                    handler.deleteAllExpediaLink(sessionUsername);
                } else {
                    handler.deleteExpediaLink(id);
                }
                response.sendRedirect("/expediahistory");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
