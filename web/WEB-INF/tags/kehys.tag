<%@tag import="kontrollerit.IstuntoServlet"%>
<%@tag import="kontrollerit.tyokalut.Valvoja"%>
<%@tag import="mallit.java.Jasen"%>
<%@tag import="mallit.java.Kayttajataso"%>
<%@tag description="Sivupohja dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@attribute name="otsikko" required="true"%>
<%@attribute name="fooruminNimi" required="true"%>
<!DOCTYPE html>
<html>
    <!-- Tämä lähdekoodi on suunniteltu katseltavaksi 80 merkin rivin leveydellä
    ja neljän merkin sarkaimen leveydellä. Pahoitteluni paikoitellen rumasta
    sisennyksestä. -->
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="John Lång">
        <meta name="description" content="Esimerkki tekemästäni
              foorumiohjelmistosta.">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/tyylit/oletus/kehys.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/tyylit/oletus/sisalto.css">
        <title>${otsikko}</title>
    </head>
    <body>
        <div id="ylaosa">
            <h1 id="paaotsikko">${fooruminNimi}</h1>
            <nav>
                <%
                    // Tämä koodi sopisi ehkä paremmin kontrolleriin...
                    final String polku = request.getContextPath();
                    // Context pathia tarvitaan koska muuten esimerkiksi
                    // alihakemiston "/data" pyytäminen rikkoisi linkit.
                    out.println("<a class=\"painike\" href=\"" + polku
                            + "/etusivu\">Etusivu</a>");
                    final Kayttajataso taso;
                    final int kayttajanumero;
                    if (request.getSession().getAttribute("jasen") != null) {
                        final Jasen jasen = (Jasen) request.getSession()
                                .getAttribute("jasen");
                        taso = jasen.annaTaso();
                        kayttajanumero = jasen.annaKayttajanumero();
                    } else {
                        taso = Kayttajataso.VIERAILIJA;
                        kayttajanumero = 0;
                    }
                    switch (taso) {
                        case YLLAPITAJA:
                            out.println("                <a class=\"painike\" "
                                    + "href=\"" + polku + "/yllapito\">Ylläpito"
                                    + "</a>");
                        case MODERAATTORI:
                        case TAVALLINEN:
                            out.println("                <a class=\"painike\" "
                                    + "href=\"" + polku + "/profiili?tunnus="
                                    + kayttajanumero + "\">Oma sivu</a>");
                            out.println("                <a class=\" painike\" "
                                    + "href=\"" + polku + "/muokkaus?ketju=0\">"
                                    + "Uusi ketju</a>");
                            out.println("                <a class=\"painike\" "
                                    + "href=\"" + polku + "/haku\">Haku</a>");
                            out.println("                <a class=\" painike\" "
                                    + "href=\"" + polku + "/istunto\">"
                                    + "Kirjaudu ulos</a>");
                                break;
                        case VIERAILIJA:
                            out.println("                <a class=\"painike\" "
                                    + "href=\"" + polku + "/istunto\"> Kirjaudu"
                                    + " / rekisteröidy</a>");
                        }
                    out.print("            ");
                    %>
            </nav>
        </div>
        <div id="alaosa">
            <jsp:doBody />
            <%="        "%>
        </div>
    </body>
</html>