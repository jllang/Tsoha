package kontrollerit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "ListausServlet", urlPatterns = {"/listaus"})
public final class ListausServlet extends HttpServlet {

    private static final List<String> DEMOKETJUT;

    static {
        DEMOKETJUT = new ArrayList<>();
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
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        kasitteleListaus(out, DEMOKETJUT);
    }

    /**
     * Tulostaa ensimmäisenä parametrina annettuun virtaan toisena parametrina
     * annetun merkkijonolistan html-taulukkona.
     *
     * @param out
     * @param lista
     */
    public static void kasitteleListaus(final PrintWriter out, final List<String> lista) {
        out.println("<div class=\"sisalto\">");
        out.println("               <table>");
        out.println("                   <tr><th>Viimeisimmät keskustelut</th></tr>");
        for (int i = 0; i < lista.size(); i++) {
            out.println("                   <tr><td class=\""
                    + (i % 2 == 0 ? "parillinen" : "pariton") + "\">"
                    + lista.get(i) + "</td></tr>");
        }
        out.println("               </table>");
        out.println("           </div>");
    }

}
