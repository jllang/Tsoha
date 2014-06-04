<%@tag import="kontrollerit.IstuntoServlet"%>
<%@tag import="mallit.yksilotyypit.Jasen"%>
<%@tag import="mallit.tyypit.Kayttajataso"%>
<%@tag description="Sivupohja dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@attribute name="otsikko" %>
<%@attribute name="fooruminNimi" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="John Lång">
        <meta name="description" content="Esimerkki tekemästäni
              foorumiohjelmistosta.">
        <link rel="stylesheet" type="text/css" href="tyylit/oletus/kehys.css">
        <link rel="stylesheet" type="text/css" href="tyylit/oletus/sisalto.css">
        <title>${otsikko}</title>
    </head>
    <body>
        <div id="ylaosa">
            <h1 id="paaotsikko">${fooruminNimi}</h1>
            <nav>
                <a class="painike" href="etusivu">Etusivu</a>
                <%
                    // Tämä koodi sopisi ehkä paremmin kontrolleriin...
                    final Kayttajataso taso;
                    final String kayttajatunnus;
                    if (IstuntoServlet.aktiivinenIstunto(request)) {
                        final Jasen jasen = (Jasen) request.getSession()
                                .getAttribute("jasen");
                        taso = jasen.annaTaso();
                        kayttajatunnus = jasen.annaKayttajatunnus();
                    } else {
                        taso = Kayttajataso.VIERAILIJA;
                        kayttajatunnus = "";
                    }
                    switch (taso) {
                            case YLLAPITAJA:
                                out.println("<a class=\"painike\" "
                                        + "href=\"yllapito\">Ylläpito</a>");
                            case MODERAATTORI:
                            case TAVALLINEN:
                                out.println("<a class=\"painike\" "
                                        + "href=\"omasivu?jasen="
                                        + kayttajatunnus
                                        + "\">Oma sivu</a>");
                                out.println("<a  class=\"painike\" "
                                        + "href=\"haku\">Haku</a>");
                                out.println("<a class=\"painike\" "
                                        + "href=\"istunto\">"
                                        + "Kirjaudu ulos</a>");
                                break;
                            case VIERAILIJA:
                                out.println("<a class=\"painike\" "
                                        + "href=\"istunto\">"
                                        + "Kirjaudu / rekisteröidy</a>");
                        }
                    %>
            </nav>
        </div>
        <div id="alaosa">
            <jsp:doBody />
        </div>
    </body>
</html>