<meta name="layout" content="ucb" />

<h1><g:message code="bapps.choose.heading" /></h1>

<g:include controller="alert" action="flashSuccess" />

<h2>Choose Account</h2>

<ul class="options-list buttons">
    <g:each in="${accounts}">
    <li><g:link controller="bapps" action="choose" id="${it.getLogin().getUserName()}" title="Choose token for account ${it.getLogin().getUserName()}." class="btn" href="#"><i class="icon-cog"></i> ${it.getLogin().getUserName()}</g:link></li>
</g:each>
</ul>

