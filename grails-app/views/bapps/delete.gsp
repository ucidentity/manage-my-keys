<meta name="layout" content="ucb" />

<h1><g:message code="bapps.delete.heading" args="${[account.emailAddress]}" /></h1>
<p><g:message code="bapps.delete.generalMessage" args="${[account.emailAddress]}" /></p>

<div class="alert alert-info">
    <g:message code="bapps.delete.deleteInfo" />
</div>

<g:include controller="alert" action="flashError" />

<g:form action="delete">
<div class="form-actions">
    <input type="submit" class="btn btn-primary" name="delete" value="Delete Key"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index"><g:message code="general.returnToOptions" /></g:link>
</div>
</g:form>
