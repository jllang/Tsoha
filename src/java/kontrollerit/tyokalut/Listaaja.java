package kontrollerit.tyokalut;

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
import mallit.java.Alue;
import mallit.java.Ketju;
import mallit.java.TietokantaDAO;
import mallit.java.Yksilotyyppi;

/**
 *
 * @author John Lång <jllang@cs.helsinki.fi>
 */
public final class Listaaja {

    private static final List<ListaAlkio>   TYHJA_HAKUTULOS, VIRHEELLINEN_HAKU;
    public static final String[]            ALUEOTSIKOT, KETJUOTSIKOT;

    static {
        TYHJA_HAKUTULOS     = new LinkedList<>();
        TYHJA_HAKUTULOS.add(new ListaAlkio(0, null, "(Ei hakutuloksia.)",
                null));
        VIRHEELLINEN_HAKU   = new LinkedList<>();
        VIRHEELLINEN_HAKU.add(new ListaAlkio(0, null, "<div class=\"virhe\">"
                + "(Tapahtui virhe)</div>", null));
        ALUEOTSIKOT         = new String[]{"Nimi", "Kuvaus"};
        KETJUOTSIKOT        = new String[]{"Aihe", "Aloittaja", "Aktiivinen"};
    }

    public static List<ListaAlkio> listaa(final String kohde) {
        final List<ListaAlkio> lista = kasitteleParametrit(kohde);
        if (lista == null) {
            return VIRHEELLINEN_HAKU;
        }
        if (lista.isEmpty()) {
            return TYHJA_HAKUTULOS;
        }
        return lista;
    }

    private static List<ListaAlkio> kasitteleParametrit(final String kohde) {
        final List<ListaAlkio> lista;
        switch (kohde) {
            case "ketjut":
//                final int aluetunnus;
//                try {
//                    aluetunnus = Integer.parseInt(req.getParameter("tunnus"));
//                    Alue alue = (Alue) TietokantaDAO.tuo(Alue.class, aluetunnus);
//                } catch (NumberFormatException | NullPointerException e) {
//                    lista = null;
//                    break;
//                } catch (SQLException e) {
//                    Logger.getLogger(Listaaja.class.getName()).log(
//                            Level.SEVERE, null, e);
//                    lista = null;
//                    break;
//                }
//
//                lista = haeKetjut();
                lista = null;
                break;
            case "alueet":
            default:
                lista = haeAlueet();
        }
        return lista;
    }

    private static List<ListaAlkio> haeKetjut() {
        try {
            Yksilotyyppi[] alueet = TietokantaDAO.tuoSivu(Ketju.class, "aihe",
                    10, 0);
            List<ListaAlkio> lista = new LinkedList<>();
            for (int i = 0; i < alueet.length; i++) {
                final Yksilotyyppi ketju = alueet[i];
                if (ketju == null) {
                    break;
                }
                lista.add(new ListaAlkio(i, "/ketju?tunnus=0", ketju.listausnimi(),
                        new String[]{}));
            }
            return lista;
        } catch (SQLException e) {
            Logger.getLogger(Listaaja.class.getName()).log(Level.SEVERE,
                    null, e);
            return null;
        }
    }

    private static List<ListaAlkio> haeAlueet() {
        try {
            Yksilotyyppi[] alueet = TietokantaDAO.tuoSivu(Alue.class, "nimi",
                    10, 0);
            List<ListaAlkio> lista = new LinkedList<>();
            for (int i = 0; i < alueet.length; i++) {
                final Alue alue = (Alue) alueet[i];
                if (alue == null) {
                    break;
                }
                lista.add(new ListaAlkio(i, "/alue?tunnus=" + alue.annaTunnus(),
                        alue.annaNimi(), new String[] {alue.annaKuvaus()}));
            }
            return lista;
        } catch (SQLException e) {
            Logger.getLogger(Listaaja.class.getName()).log(Level.SEVERE,
                    null, e);
            return null;
        }
    }
}
