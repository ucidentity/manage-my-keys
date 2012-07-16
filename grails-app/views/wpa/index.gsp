<meta name="layout" content="ucb" />

<h1><g:message code="wpa.index.heading" /></h1>

<g:include controller="alert" action="flashSuccess" />

<p><g:message code="wpa.index.generalMessage" /></p>

<g:if test="${hasToken}">
<h2><g:message code="wpa.index.optionsHeading" /></h2>
</g:if>

<ul class="options-list buttons">
    <li><button class="btn btn-success" title="Set AirBears2 key..."><g:link controller="wpa" action="set" href="#"><i class="icon-pencil icon-white"></i> <g:message code="wpa.index.optionsSetLink" /></g:link></button></li>
    <g:if test="${hasToken}">
    <li><button class="btn btn-warning" title="View AirBears2 key..."><g:link controller="wpa" action="view" href="#"><i class="icon-eye-open icon-white"></i> <g:message code="wpa.index.optionsViewLink" /></g:link></button></li>
    <li><button class="btn btn-danger" title="Delete AirBears2 key..."><g:link controller="wpa" action="delete" href="#"><i class="icon-trash icon-white"></i> <g:message code="wpa.index.optionsDeleteLink" /></g:link></button></li>
</g:if>
</ul>
