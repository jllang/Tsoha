//package kontrollerit;
//
//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import kontrollerit.tyokalut.Uudelleenohjaaja;
//import kontrollerit.tyokalut.Valvoja;
//
///**
// *
// * @author John Lång (jllang@cs.helsinki.fi)
// */
//@WebServlet(name = "HakuServlet", urlPatterns = {"/haku"})
//public final class HakuServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(final HttpServletRequest req,
//            final HttpServletResponse resp) throws ServletException,
//            IOException {
//        kasittelePyynto(req, resp);
//    }
//
//    @Override
//    protected void doGet(final HttpServletRequest req,
//            final HttpServletResponse resp) throws ServletException,
//            IOException {
//        kasittelePyynto(req, resp);
//    }
//
//    private void kasittelePyynto(final HttpServletRequest req,
//            final HttpServletResponse resp) throws ServletException,
//            IOException {
////        resp.setContentType("text/html;charset=UTF-8");
////        resp.setCharacterEncoding("UTF-8");
////        req.setCharacterEncoding("UTF-8");
//        if (Valvoja.aktiivinenIstunto(req, resp, "haku")) {
//
//        }
//    }
//
//}
