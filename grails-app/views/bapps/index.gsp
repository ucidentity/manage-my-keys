<meta name="layout" content="ucb" />

<h1><g:message code="bapps.index.heading" args="${[account.getLogin().getUserName()]}" /></h1>

<g:include controller="alert" action="flashSuccess" />

<p><g:message code="bapps.index.generalMessage" /></p>

<h2><g:message code="bapps.index.optionsHeading" /></h2>

<ul class="options-list buttons">
    <li><g:link controller="bapps" action="set" id="${account.getLogin().getUserName()}" title="Set key for account ${account.getLogin().getUserName()}." class="btn btn-success" href="#"><i class="icon-pencil icon-white"></i> <g:message code="bapps.index.optionsSetLink" /></g:link></li>
    <li><g:link controller="bapps" action="delete" id="${account.getLogin().getUserName()}" title="Delete key for account ${account.getLogin().getUserName()}." class="btn btn-danger" href="#"><i class="icon-trash icon-white"></i> <g:message code="bapps.index.optionsDeleteLink" /></g:link></li>
</ul>

<g:if test="${session.googleAppsAccounts.size() > 1}">
<p><g:link controller="bapps" action="choose"><g:message code="bapps.index.returnToAccountsLink" /></g:link>
</g:if>
