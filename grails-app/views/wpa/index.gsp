<meta name="layout" content="ucb" />

<h2><g:message code="wpa.index.heading" /></h2>

<g:include controller="alert" action="flashSuccess" />

<p><g:message code="wpa.index.generalMessage" /></p>

<h3><g:message code="wpa.index.optionsHeading" /></h3>

<ul class="options-list buttons">
    <li><g:link controller="wpa" action="set" title="Set WPA key." class="btn btn-success" href="#"><i class="icon-pencil icon-white"></i> <g:message code="wpa.index.optionsSetLink" /></g:link></li>
    <g:if test="${hasToken}">
    <li><g:link controller="wpa" action="view" title="View WPA key." class="btn btn-warning" href="#"><i class="icon-eye-open icon-white"></i> <g:message code="wpa.index.optionsViewLink" /></g:link></li>
    <li><g:link controller="wpa" action="delete" title="Delete WPA key." class="btn btn-danger" href="#"><i class="icon-trash icon-white"></i> <g:message code="wpa.index.optionsDeleteLink" /></g:link></li>
</g:if>
</ul>
