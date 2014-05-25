package kontrollerit;


import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
//@WebServlet(name = "ProfiiliServlet", urlPatterns = {"/kayttaja"})
public class ProfiiliServlet extends HttpServlet {

//    @Override
//    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
//
//    }

    public static void demoKayttaja(final PrintWriter out) {
        out.println("           <table class=\"parillinen sisalto\">");
        out.println("               <tr>");
        out.println("                   <td class=\"viesti\">&lt;Käyttäjätunnus"
                + "&gt;</td>");
        out.println("                   <td class=\"avatar\" rowspan=\"4\">"
                + "<img src=\"data/paikanpitaja.png\" alt=\"&lt;Nimimerkki&gt;"
                + ":n Avatar\"></td>");
        out.println("               </tr>");
        out.println("               <tr>");
        out.println("                   <td class=\"viesti\">&lt;Taso&gt;</td>");
        out.println("               </tr>");
        out.println("               <tr>");
        out.println("                   <td class=\"viesti\">&lt;Rekisteröity"
                + "&gt;</td>");
        out.println("               </tr>");
        out.println("               <tr>");
        out.println("                   <td class=\"viesti\">&lt;Viestejä&gt;"
                + "</td>");
        out.println("               </tr>");
        out.println("               <tr>");
        out.println("                   <td class=\"viesti\" colspan=\"2\">&lt;"
                + "Kuvaus&gt;</td>");
        out.println("               </tr>");
        out.println("           </table>");
    }
}
