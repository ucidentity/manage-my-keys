<meta name="layout" content="ucb" />

<h1><g:message code="bapps.index.heading" args="${[account.getLogin().getUserName()]}" /></h1>

<g:if test="${calMailAccount.loginDisabled == true}">
<div class="alert alert-info">
    <h3><g:message code="bapps.index.disabledHeader" /></h3>
    <g:message code="bapps.index.disabledMessage" />
</div>
</g:if>
<g:else>
    <g:include controller="alert" action="flashSuccess" />

    <p><g:message code="bapps.index.generalMessage" /></p>

    <div class="alert alert-info"><p><g:message code="bapps.index.deleteNote" /></p></div>

    <h2><g:message code="bapps.index.optionsHeading" /></h2>

    <ul class="options-list buttons">
        <li>
            <g:form controller="bapps" action="set" id="${account.getLogin().getUserName()}" method="get" name="set">
                <button type="submit" class="btn btn-success" title="Set bConnected key for account ${account.getLogin().getUserName()}...">
                    <i class="icon-pencil icon-white"></i> <g:message code="bapps.index.optionsSetLink" />
                </button>
            </g:form>
        </li>
        <li>
            <g:form controller="bapps" action="delete" id="${account.getLogin().getUserName()}" method="get" name="delete">
                <button type="submit" class="btn btn-danger" title="Delete bConnected key for account ${account.getLogin().getUserName()}...">
                    <i class="icon-trash icon-white"></i> <g:message code="bapps.index.optionsDeleteLink" />
                </button>
            </g:form>
        </li>
    </ul>
</g:else>

<g:if test="${session.googleAppsAccounts.size() > 1}">
<p><g:link controller="bapps" action="choose"><g:message code="bapps.index.returnToAccountsLink" /></g:link>
</g:if>