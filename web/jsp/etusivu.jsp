<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <t:listaus listanNimi="Keskustelualueet" lista="${aluelista}"
                       lisakenttia="1" otsikot="${aluelistanOtsikot}"
                       hakupalkki="false" />
            <t:listaus listanNimi="Tuoreimmat ketjut" lista="${ketjulista}"
                       lisakenttia="2" otsikot="${ketjulistanOtsikot}"
                       hakupalkki="false" />
            <t:listaus listanNimi="Tilastoja" lista="${tilastolista}"
                       lisakenttia="1" otsikot="${tilastolistanOtsikot}"
                       hakupalkki="false" />
</t:kehys>