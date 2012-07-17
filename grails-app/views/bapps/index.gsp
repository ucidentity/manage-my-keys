<meta name="layout" content="ucb" />

<h1><g:message code="bapps.index.heading" args="${[account.getLogin().getUserName()]}" /></h1>

<g:include controller="alert" action="flashSuccess" />

<p><g:message code="bapps.index.generalMessage" /></p>

<div class="alert alert-info"><p><g:message code="bapps.index.deleteNote" /></p></div>

<h2><g:message code="bapps.index.optionsHeading" /></h2>

<ul class="options-list buttons">
    <li><button class="btn btn-success" title="Set key for account ${account.getLogin().getUserName()}..."><g:link controller="bapps" action="set" id="${account.getLogin().getUserName()}" href="#"><i class="icon-pencil icon-white"></i> <g:message code="bapps.index.optionsSetLink" /></g:link></button></li>
    <li><button class="btn btn-danger" title="Delete key for account ${account.getLogin().getUserName()}..."><g:link controller="bapps" action="delete" id="${account.getLogin().getUserName()}" href="#"><i class="icon-trash icon-white"></i> <g:message code="bapps.index.optionsDeleteLink" /></g:link></button></li>
</ul>

<g:if test="${session.googleAppsAccounts.size() > 1}">
<p><g:link controller="bapps" action="choose"><g:message code="bapps.index.returnToAccountsLink" /></g:link>
</g:if>
