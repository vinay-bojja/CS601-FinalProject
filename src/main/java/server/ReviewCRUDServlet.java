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
import java.util.Set;

public class ReviewCRUDServlet extends HttpServlet {
    /**
     * Calls this method on get Request: "/reviewscrud"
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

                ReviewsDatabaseHandler handler = ReviewsDatabaseHandler.getInstance();
                Set<Review> reviews = handler.getAllReviewsByUsername(sessionUsername);

                Template template = ve.getTemplate("templates/reviewscrud.html");
                if (reviews.size() != 0) {
                    context.put("reviews", reviews);
                } else {
                    context.put("reviews", "");
                }
                context.put("lastLoginDate", lastLoginDate.trim());

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
