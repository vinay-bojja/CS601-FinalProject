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

public class EditReviewServlet extends HttpServlet {

    /**
     * Calls this method on Get request:"/editreview".
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
                String reviewId = request.getParameter("reviewid");
                String doPostServletName = request.getServletPath() + "?reviewid=" + reviewId;

                Template template = ve.getTemplate("templates/editreview.html");

                //set context
                context.put("doPostServlet", doPostServletName);
                context.put("lastLoginDate", lastLoginDate.trim());

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls this method on Post request:"/editreview".
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String sessionUsername = SessionHandler.getSession(request, "username");

            if (sessionUsername == null) {
                response.sendRedirect("/login");
            } else {
                String reviewId = request.getParameter("reviewid");
                String reviewText = request.getParameter("reviewtext");
                String reviewTitle = request.getParameter("title");

//                Object collection2 = getServletContext().getAttribute("collection2");
//                ((ThreadSafeReviewCollection) collection2).editReview(reviewId, sessionUsername, reviewTitle, reviewText);

                ReviewsDatabaseHandler handler = ReviewsDatabaseHandler.getInstance();
                handler.editReview(reviewId, reviewTitle, reviewText);

                response.sendRedirect("/reviewscrud");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
