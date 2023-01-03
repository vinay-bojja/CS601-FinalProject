package server;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * This class uses Jetty & servlets to implement server serving hotel and review info
 */
public class JettyServer {
    private static final int PORT = 8090;

    public JettyServer() {
    }

    /**
     * Function that starts the Jetty server
     *
     * @throws Exception throws exception if access failed
     */
    public void start() {
        Server server = new Server(PORT);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.addServlet(UserRegistrationServlet.class, "/register-user");
        handler.addServlet(UserLoginServlet.class, "/login");
        handler.addServlet(HomepageServlet.class, "/homepage");
        handler.addServlet(SignOutServlet.class, "/signout");
        handler.addServlet(HotelSearchServlet.class, "/hotelsearch");
        handler.addServlet(HotelInfoServlet.class, "/hotelinfo");
        handler.addServlet(ReviewCRUDServlet.class, "/reviewscrud");
        handler.addServlet(AddReviewServlet.class, "/addreview");
        handler.addServlet(EditReviewServlet.class, "/editreview");
        handler.addServlet(DeleteReviewServlet.class, "/deletereview");
        handler.addServlet(ExpediaVisitHistory.class, "/expediahistory");
        handler.addServlet(DeleteExpediaVisitHistory.class, "/deleteexpeditevisithistory");
        handler.addServlet(FavoriteHotelsServlet.class, "/favoritehotels");

        //For Ajax calls
        handler.addServlet(GetReviewServlet.class, "/getReviews");
        handler.addServlet(AddToExpediaHistoryTableServlet.class, "/insertIntoHistoryTable");
        handler.addServlet(AddHotelToFavoriteServlet.class, "/addHotelToFavorites");
        handler.addServlet(GetFavoriteHotel.class, "/getFavoriteHotel");
        handler.addServlet(DeleteFavoriteHotelsServlet.class, "/deletefavoritehotels");
        handler.addServlet(GetUserAllFavoriteHotels.class, "/getuserallfavoritehotels");

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        handler.setAttribute("templateEngine", velocity);

        ResourceHandler resource_handler = new ResourceHandler(); // a handler for serving static pages
        resource_handler.setDirectoriesListed(true);

        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, handler});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
