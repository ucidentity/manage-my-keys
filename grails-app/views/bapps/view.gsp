<meta name="layout" content="ucb" />

<h1><g:message code="bapps.viewPage.heading" args="${[account.getLogin().getUserName()]}" /></h1>

<p>
    <g:message code="bapps.viewPage.generalMessage" args="${[account.getLogin().getUserName()]}" />
</p>

<p>
    <div class="token-value">${flash.token}</div>
</p>

<div class="alert alert-warning">
    <g:message code="bapps.viewPage.pageRefreshWarning" />
</div>

<p><g:link controller="bapps" action="index" id="${account.getLogin().getUserName()}"><g:message code="bapps.viewPage.returnToIndexLink" /></g:link>
