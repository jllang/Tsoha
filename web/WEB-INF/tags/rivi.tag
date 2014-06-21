<%--
    Document   : alkio
    Created on : 5.6.2014, 22:18:28
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@tag description="Listauksen rivi dynaamisella sisällöllä."
       pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@tag import="kontrollerit.tyypit.ListaAlkio"%>
<%@attribute name="lisakenttia" type="Integer" required="true"%>
<%@attribute name="alkio" type="ListaAlkio" required="true"%>
<%
    out.println("<tr class=\"" + alkio.parillisuus + "\">");
    out.print("                        <td class=\"peruskentta\">");
    if (alkio.url != null) {
        out.print("<a href=\"" + alkio.url + "\">"
                + alkio.nimi + "</a>");
    } else {
        out.print(alkio.nimi);
    }
    out.println("</td>");
    if (alkio.lisakentat != null) {
        for (String kentta : alkio.lisakentat) {
            out.print("                        <td class=\"peruskentta\">");
            out.print(kentta == null ? "" : kentta);
            out.println("</td>");
        }
    } else {
        for (int i = 0; i < lisakenttia; i++) {
            out.println("                        <td></td>");
        }
    }
    out.println("                    </tr>");
    out.print("                    ");
%>