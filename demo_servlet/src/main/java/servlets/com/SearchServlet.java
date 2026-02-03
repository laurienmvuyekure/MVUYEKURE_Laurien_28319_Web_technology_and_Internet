package servlets.com;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = request.getParameter("searchTerm");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String encoded = java.net.URLEncoder.encode(searchTerm, "UTF-8");
            response.sendRedirect("https://www.google.com/search?q=" + encoded);
        } else {
            response.sendRedirect("https://www.google.com");
        }
    }
}
