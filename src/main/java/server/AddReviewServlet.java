package server;

import commmonFunctions.SessionHandler;
import dboperations.ReviewsDatabaseHandler;
import hotelapp.Review;
import hotelapp.ThreadSafeReviewCollection;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AddReviewServlet extends HttpServlet {

    /**
     * Calls this method on Get request:"/addreview".
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
                String doPostServletName = request.getServletPath();
                String errorMessage = request.getParameter("error");

                Template template = ve.getTemplate("templates/addreview.html");

                //Context
                context.put("doPostServlet", doPostServletName);
                if (errorMessage == null) {
                    context.put("errormessage", "");
                } else {
                    context.put("errormessage", errorMessage);
                }
                context.put("lastLoginDate", lastLoginDate);

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls this method on Post request:"/addreview".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String hotelId = request.getParameter("hotelId");
            String title = request.getParameter("title");
            String reviewText = request.getParameter("reviewtext");
            String rating = request.getParameter("rating");
            String sessionUsername = SessionHandler.getSession(request, "username");

            ReviewsDatabaseHandler reviewHandler = ReviewsDatabaseHandler.getInstance();
            String errorMessage = reviewHandler.generateReviewIdAndInsertReview(sessionUsername, hotelId, title, reviewText, rating);


            if (errorMessage == null) {
                response.sendRedirect("/reviewscrud");
            } else {
                response.sendRedirect("/addreview?error=" + errorMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
