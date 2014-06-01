//
//package kontrollerit;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// *
// * @author John Lång <jllang@cs.helsinki.fi>
// */
//@WebServlet(name = "NavigaatioServlet", urlPatterns = {"/navigaatio"})
//public class NavigaatioServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//        resp.setContentType("text/html;charset=UTF-8");
////        req.setAttribute("fooruminNimi", "Esimerkkifoorumi");
////        RequestDispatcher lahettaja = req.getRequestDispatcher("/Tsoha/WEB-INF/"
////                + "jspf/navigaatio.jspf");
////        lahettaja.forward(req, resp);
//        resp.sendRedirect("jsp/navigaatio.jspf");
//    }
//
//}
