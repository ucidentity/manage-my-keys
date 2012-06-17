<meta name="layout" content="ucb" />

<h2><g:message code="bapps.setPage.heading" args="${[account.getLogin().getUserName()]}"/></h2>
<p><g:message code="bapps.setPage.generalMessage" args="${[account.getLogin().getUserName()]}"/></p>

<g:include controller="alert" action="formErrors" params="['item':googleApps]" />
<g:include controller="alert" action="flashError" />

<div id="form">
    <g:render template="form" />
</div>
