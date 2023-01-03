package commmonFunctions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHandler {
    /**
     * Returns session value of key.
     *
     * @param request
     * @param key
     * @return
     */
    public static String getSession(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(key);
    }

    /**
     * Sets session.
     *
     * @param request
     * @param key
     * @param value
     */
    public static void setSession(HttpServletRequest request, String key, String value) {
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
    }
}
