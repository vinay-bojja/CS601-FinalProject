package server;

import commmonFunctions.SessionHandler;
import dboperations.ReviewsDatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class GetReviewServlet extends HttpServlet {
    public static final String LIMIT = "6";

    /**
     * Returns Reviews in Json format.
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String offset = SessionHandler.getSession(request, "offset");
            String limit = SessionHandler.getSession(request, "limit");
            PrintWriter out = response.getWriter();
            String hotelId = request.getParameter("hotelId");
            hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            String isNext = request.getParameter("isNext");
            ReviewsDatabaseHandler handler = ReviewsDatabaseHandler.getInstance();
            if (isNext.equals("null")) {
                String reviewsJSON = handler.getPaginatedReviews(hotelId, LIMIT, "0");
                SessionHandler.setSession(request, "offset", "0");
                SessionHandler.setSession(request, "limit", LIMIT);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                out.println(reviewsJSON);
            } else if (isNext.equals("true")) {
                String newOffSet = String.valueOf(Integer.parseInt(offset) + Integer.parseInt(limit));
                String reviewsJSON = handler.getPaginatedReviews(hotelId, limit, newOffSet);
                if (reviewsJSON != null) {
                    SessionHandler.setSession(request, "offset", newOffSet);
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println(reviewsJSON);
                }

            } else if (isNext.equals("false")) {
                if (Integer.parseInt(offset) > 0) {
                    String newOffSet = String.valueOf(Integer.parseInt(offset) - Integer.parseInt(limit));
                    String reviewsJSON = handler.getPaginatedReviews(hotelId, limit, newOffSet);
                    SessionHandler.setSession(request, "offset", newOffSet);
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println(reviewsJSON);
                } else {
                    String newOffSet = "0";
                    String reviewsJSON = handler.getPaginatedReviews(hotelId, limit, newOffSet);
                    SessionHandler.setSession(request, "offset", newOffSet);
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println(reviewsJSON);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
