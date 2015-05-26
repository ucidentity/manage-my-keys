<meta name="layout" content="ucb"/>

<h1><g:message code="bapps.index.heading" args="${[account.emailAddress]}"/></h1>

<g:include controller="alert" action="flashSuccess"/>

<p><g:message code="bapps.index.generalMessage"/></p>

<div class="alert alert-info"><p><g:message code="bapps.index.deleteNote"/></p></div>

<h2><g:message code="bapps.index.optionsHeading"/></h2>

<ul class="options-list buttons">
    <li>
        <g:form controller="bapps" action="set" method="get" name="set">
            <button type="submit" class="btn btn-success" title="Set bConnected key for account ${account.emailAddress}...">
                <i class="icon-pencil icon-white"></i> <g:message code="bapps.index.optionsSetLink"/>
            </button>
        </g:form>
    </li>
    <li>
        <g:form controller="bapps" action="delete" method="get" name="delete">
            <button type="submit" class="btn btn-danger" title="Delete bConnected key for account ${account.emailAddress}...">
                <i class="icon-trash icon-white"></i> <g:message code="bapps.index.optionsDeleteLink"/>
            </button>
        </g:form>
    </li>
</ul>

