<meta name="layout" content="ucb" />

<h1><g:message code="bapps.choose.heading" /></h1>

<g:include controller="alert" action="flashSuccess" />

<h2>Choose Account</h2>

<ul class="options-list buttons">
    <g:each in="${accounts}">
    <li><button class="btn" title="Choose token for account ${it.getLogin().getUserName()}."><g:link controller="bapps" action="choose" id="${it.getLogin().getUserName()}" href="#"><i class="icon-cog"></i> ${it.getLogin().getUserName()}</g:link></button></li>
</g:each>
</ul>

