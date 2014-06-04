package kontrollerit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyypit.ListaAlkio;
import mallit.TietokantaDAO;
import mallit.yksilotyypit.Alue;
import mallit.yksilotyypit.Yksilotyyppi;

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
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        req.setAttribute("listanNimi", "Keskustelualueet");
        req.setAttribute("lista", haeLista());
        Uudelleenohjaaja.siirra(req, resp, "/jsp/listaus.jsp");
    }

    private static List<ListaAlkio> haeLista() {
        List<ListaAlkio> lista = new LinkedList<>();

        try {
            Yksilotyyppi[] alueet = TietokantaDAO.haeSivu(Alue.class, "nimi",
                    10, 0);
            for (int i = 0; i < alueet.length; i++) {
                final Alue alue = (Alue) alueet[i];
                if (alue == null) {
                    break;
                }
                lista.add(new ListaAlkio(i, "/etusivu", alue.annaNimi()));
            }
            return lista;
        } catch (SQLException e) {
            Logger.getLogger(ListausServlet.class.getName()).log(Level.SEVERE,
                    null, e);
            lista.add(new ListaAlkio(0, "/etusivu", "<t:virhe>Palvelimella "
                    + "tapahtui virhe</t:virhe>"));
        }
        return lista;
    }
}
