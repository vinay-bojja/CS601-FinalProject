package server;

import commmonFunctions.SessionHandler;
import dboperations.HotelsDatabaseHandler;
import dboperations.ReviewsDatabaseHandler;
import hotelapp.Hotel;
import hotelapp.Review;
import hotelapp.ThreadSafeHotelCollection;
import hotelapp.ThreadSafeReviewCollection;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Set;

public class HotelInfoServlet extends HttpServlet {
    /**
     * Calls this method on Get request: "/hotelinfo".
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
                String hotelId = request.getParameter("hotelId");
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);

                HotelsDatabaseHandler hotelHandler = HotelsDatabaseHandler.getInstance();
                Hotel hotel = hotelHandler.getHotelById(hotelId);

                ReviewsDatabaseHandler reviewHandler = ReviewsDatabaseHandler.getInstance();
                Set<Review> reviews = reviewHandler.getAllReviewsByHotelId(hotelId);
                Double averageRating = getAverageRating(reviews);

                String expediaLink = "https://www.expedia.com/" + hotel.getCity() + "-Hotels-";
                String hotelName = hotel.getHotelName().replace(" ", "-").replace("/", "");
                expediaLink = expediaLink + hotelName + ".h" + hotel.getHotelId() + ".Hotel-Information";


                VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
                VelocityContext context = new VelocityContext();
                context.put("hotel", hotel);
                context.put("username", sessionUsername);
                if (reviews != null) {
                    context.put("reviews", reviews);
                } else {
                    context.put("reviews", "");
                }
                context.put("averagerating", averageRating);
                context.put("expedialink", expediaLink);
                context.put("lastLoginDate", lastLoginDate.trim());

                Template template = ve.getTemplate("templates/hotelinfo.html");
                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Calls this method on Post request: "/hotelinfo".
     *
     * @param reviews
     * @return
     */
    public double getAverageRating(Set<Review> reviews) {
        if (reviews == null || reviews.size() == 0) {
            return 0;
        } else {
            double sum = 0;
            for (Review review : reviews) {
                sum = sum + review.getRatingOverall();
            }

            DecimalFormat df = new DecimalFormat("###.#");
            return Double.parseDouble(df.format(sum / reviews.size()));
        }
    }
}
