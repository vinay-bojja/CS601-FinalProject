package server;


import commmonFunctions.SessionHandler;
import dboperations.DatabaseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignOutServlet extends HttpServlet {

    /**
     * Calls this method on Get Request: "/signout"
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String loginDateTime = SessionHandler.getSession(request, "loginDateTime");
            String username = SessionHandler.getSession(request, "username");
            DatabaseHandler handler = DatabaseHandler.getInstance();
            handler.insertOrUpdateLoginDateTime(loginDateTime, username);
            SessionHandler.setSession(request, "username", null);

            response.sendRedirect("/login?didsignout=1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
