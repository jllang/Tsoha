package kontrollerit;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kontrollerit.tyokalut.Listaaja;
import kontrollerit.tyokalut.Otsikoija;
import kontrollerit.tyokalut.Uudelleenohjaaja;
import kontrollerit.tyokalut.Valvoja;
import kontrollerit.tyypit.ListaAlkio;
import mallit.java.Alue;
import mallit.java.Jasen;
import mallit.java.Ketju;
import mallit.java.Viesti;

/**
 *
 * @author John Lång (jllang@cs.helsinki.fi)
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/etusivu"})
public final class IndexServlet extends HttpServlet {

    private static final Object TILASTOLUKKO = new Object();
    private static final String[] TILASTO_OTSIKOT = {"Avain", "Arvo"};
    private static final long PAIVITYSVALI = 300000L; // 5 min
    private static long paivityshetki = 0;
    private static List<ListaAlkio> alueet, ketjut, tilastot;

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
//        resp.setContentType("text/html;charset=UTF-8");
//        resp.setCharacterEncoding("UTF-8");
//        req.setCharacterEncoding("UTF-8");
        Otsikoija.asetaOtsikko(req, "Etusivu");
        paivitaNakyma(req);
        Uudelleenohjaaja.siirra(req, resp, "jsp/etusivu.jsp");
    }

    private void paivitaNakyma(final HttpServletRequest req) {
        synchronized (TILASTOLUKKO) {
            if (paivityshetki == 0
                    || System.currentTimeMillis() - paivityshetki >= PAIVITYSVALI) {
                alueet = Listaaja.listaa("alueet");
                ketjut = Listaaja.listaa("tuoreet");
                paivityshetki = System.currentTimeMillis();
                tilastot = new LinkedList<>();
                tilastot.add(new ListaAlkio(0, null, "Rekisteröityjä "
                        + "käyttäjätunnuksia", new String[]{
                            "" + Jasen.lukumaara()}));
                tilastot.add(new ListaAlkio(1, null, "Kirjautuneita "
                        + "käyttäjätunnuksia", new String[]{
                            "" + Valvoja.annaKirjautuneet().size()}));
                tilastot.add(new ListaAlkio(2, null, "Keskustelualueita",
                        new String[]{"" + Alue.lukumaara()}));
                tilastot.add(new ListaAlkio(3, null, "Viestiketjuja",
                        new String[]{"" + Ketju.lukumaara()}));
                tilastot.add(new ListaAlkio(4, null, "Viestejä ketjuissa",
                        new String[]{"" + Viesti.lukumaara()}));
                tilastot.add(new ListaAlkio(5, null, "Etusivun päivitysajankohta",
                        new String[]{DateFormat.getInstance()
                            .format(new Date(paivityshetki))}));
            }
        }
        req.setAttribute("aluelista", alueet);
        req.setAttribute("aluelistanOtsikot", Listaaja.ALUEOTSIKOT);
        req.setAttribute("ketjulista", ketjut);
        req.setAttribute("ketjulistanOtsikot", Listaaja.KETJUOTSIKOT);
        req.setAttribute("tilastolista", tilastot);
        req.setAttribute("tilastolistanOtsikot", TILASTO_OTSIKOT);
    }
}
