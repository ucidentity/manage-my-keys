<meta name="layout" content="ucb" />
<h2><g:message code="wpa.setPage.heading" /></h2>
<p><g:message code="wpa.setPage.generalMessage" /></p>

<g:include controller="alert" action="formErrors" params="['item':wpa]" />
<g:include controller="alert" action="flashError" />

<div id="form">
    <g:render template="form" />
</div>
