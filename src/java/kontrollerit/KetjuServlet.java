
package kontrollerit;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
@WebServlet(name = "KetjuServlet", urlPatterns = {"/ketju"})
public final class KetjuServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
    }

    public static void kasitteleKetju(final PrintWriter out, final List<String> lista) {
        for (int i = 0; i < lista.size(); i++) {
            out.println("           <div class=\""
                    + (i % 2 == 0 ? "parillinen" : "pariton") + " sisalto\">");
            out.println("               <table>");
            out.println("                   <tr>");
            out.println("                       <th>&lt;<a href=\"kayttaja\">"
                    + "Nimimerkki</a>&gt;</th>");
//            out.println("                       <th>&lt;Alue 1&gt;[, &lt;Alue "
//                    + "2&gt;, &lt;Alue 3&gt;, ..., &lt;Alue n&gt;]</th>");
            out.println("                       <th></th>");
            out.println("                   </tr>");
            out.println("                   <tr>");
            out.println("                       <td class=\"avatar\">"
                    + "<a href=\"kayttaja\"><img src=\"data/paikanpitaja.png\" "
                    + "alt=\"Avatar\"></a></td>");
            out.println("                       <td class=\"viesti\">"
                    + lista.get(i) + "</td>");
            out.println("                   </tr>");
            out.println("                   <tr>");
            out.println("                       <td>&lt;Kirjoitettu&gt;</td>");
            out.println("                       <td>[&lt;Muokattu&gt;"
                    + "|&lt;Moderoitu&gt;]</td>");
            out.println("                   </tr>");
            out.println("               </table>");
            out.println("           </div>");
            out.println("           <br>");
        }
    }
}
