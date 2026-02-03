package servlets.com;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (password == null || password.length() < 8) {
            out.println("<h3>Hello " + username + ", your password is weak. Try a strong one.</h3>");
        } else {
            out.println("<h2>Welcome " + username + "</h2>");
        }

        out.println("<br><a href='index.html'>Back to Home</a>");
    }
}
