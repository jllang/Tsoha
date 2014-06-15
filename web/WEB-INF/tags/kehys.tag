<%@tag import="kontrollerit.IstuntoServlet"%>
<%@tag import="kontrollerit.tyokalut.Valvoja"%>
<%@tag import="mallit.java.Jasen"%>
<%@tag import="mallit.java.Kayttajataso"%>
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
                <%
                    // Tämä koodi sopisi ehkä paremmin kontrolleriin...
                    out.println("<a class=\"painike\" href=\"etusivu\">Etusivu"
                            + "</a>");
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
                                out.println("                <a class=\""
                                        + "painike\" href=\"yllapito\">"
                                        + "Ylläpito</a>");
                            case MODERAATTORI:
                            case TAVALLINEN:
                                out.println("                <a class=\""
                                        + "painike\" href=\"profiili?tunnus="
                                        + kayttajanumero + "\">Oma sivu</a>");
                                out.println("                <a class=\""
                                        + "painike\" href=\"muokkaus?ketju=0\">"
                                        + "Uusi ketju</a>");
                                out.println("                <a class=\""
                                        + "painike\" href=\"haku\">Haku</a>");
                                out.println("                <a class=\""
                                        + "painike\" href=\"istunto\">"
                                        + "Kirjaudu ulos</a>");
                                break;
                            case VIERAILIJA:
                                out.println("                <a class=\""
                                        + "painike\" href=\"istunto\">"
                                        + "Kirjaudu / rekisteröidy</a>");
                        }
                    out.print("            ");
                    %>
            </nav>
        </div>
        <div id="alaosa">
            <jsp:doBody />
            <% out.print("        "); %>
        </div>
    </body>
</html>