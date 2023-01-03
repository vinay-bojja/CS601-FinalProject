package server;

import commmonFunctions.SessionHandler;
import dboperations.ReviewsDatabaseHandler;
import hotelapp.ThreadSafeReviewCollection;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DeleteReviewServlet extends HttpServlet {

    /**
     * Calls this method on Post request:"/deletereview".
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
                String reviewId = request.getParameter("reviewid");

                ReviewsDatabaseHandler handler = ReviewsDatabaseHandler.getInstance();
                handler.deleteReview(reviewId);
                response.sendRedirect("/reviewscrud");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
