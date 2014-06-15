<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
    <h2>Alueet</h2>
    <t:listaus listanNimi="Keskustelualueet" lista="${aluelista}"
               lisakenttia="1" otsikot="${aluelistanOtsikot}" />
    <h2>Tuoreimmat ketjut</h2>
    <t:listaus listanNimi="Tuoreimmat ketjut" lista="${ketjulista}"
               lisakenttia="2" otsikot="${ketjulistanOtsikot}" />
    <h2>Tilastollista triviaa</h2>
    <t:listaus listanNimi="Tilastoja" lista="${tilastolista}"
               lisakenttia="1" otsikot="${tilastolistanOtsikot}" />
</t:kehys>