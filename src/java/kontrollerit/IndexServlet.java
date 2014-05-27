
package kontrollerit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/etusivu", "/esimketju", "/kayttaja"})
public final class IndexServlet extends HttpServlet {

    private static final ArrayList<String> DEMOALUEET, DEMOKETJUT, DEMOVIESTIT;

    static {
        DEMOALUEET = new ArrayList<String>();
        DEMOKETJUT = new ArrayList<String>();
        DEMOKETJUT.add("<a href=\"esimketju\">Esimerkkiketju</a>");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("&lt;Ketjun nimi&gt;");
        DEMOKETJUT.add("...");
        DEMOVIESTIT = new ArrayList<String>();
        DEMOVIESTIT.add("Tältä näytää ketjun ensimmäinen viesti. Ensimmäinen "
                + "viesti määrää ketjun aiheen.");
        DEMOVIESTIT.add("Tältä näyttää ketjun toinen viesti.");
        DEMOVIESTIT.add("&lt;Viesti&gt;");
        DEMOVIESTIT.add("&lt;Viesti&gt;");
        DEMOVIESTIT.add("&lt;Viesti&gt;");
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = resp.getWriter();
        final String pyydettySivu = req.getServletPath();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("   <head>");
        out.println("       <meta charset=\"UTF-8\">");
        out.println("       <meta name=\"author\" content=\"John Lång\">");
        out.println("       <meta name=\"description\" content=\"Esimerkki"
                + " tekemästäni keskustelufoorumiohjelmistosta.\">");
        out.println("       <title> " + req.getRemoteUser()
                + "@" + "esimerkkifoorumi" + ": " + pyydettySivu
                + "</title>");
        out.println("       <link rel=\"stylesheet\" type=\"text/css\""
                + " href=\"tyylit/oletus/index.css\">");
        out.println("   </head>");
        out.println("   <body>");
        out.println("       <div id=\"alaosa\">");
        switch (pyydettySivu) {
            case "/esimketju":
                out.println("           <h1>&lt;Ketjun aihe&gt;</h1>");
                out.println("           <h2>&lt;Alue 1&gt;[, &lt;Alue 2&gt;, "
                        + "&lt;Alue 3&gt;, ... , &lt;Alue n&gt;]</h2>");
                KetjuServlet.kasitteleKetju(out, DEMOVIESTIT);
                break;
            case "/kayttaja":
                out.println("            <h1>&lt;Nimimerkki&gt;:n profiili</h1>");
                ProfiiliServlet.demoKayttaja(out);
                break;
            default:
                out.println("           <h1>Etusivu</h1>");
                ListausServlet.kasitteleListaus(out, DEMOKETJUT);
        }
        out.println("       </div>");
        out.println("   </body>");
        out.println("</html>");
    }
}
