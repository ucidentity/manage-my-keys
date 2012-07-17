<meta name="layout" content="ucb" />

<h1><g:message code="bapps.delete.heading" args="${[account.getLogin().getUserName()]}" /></h1>
<p><g:message code="bapps.delete.generalMessage" args="${[account.getLogin().getUserName()]}" /></p>

<p>
<div class="alert alert-info">
    <g:message code="bapps.delete.deleteInfo" />
</div>
</p>

<g:include controller="alert" action="flashError" />

<g:form action="delete" id="${account.getLogin().getUserName()}">
<div class="form-actions">
    <input type="submit" class="btn btn-primary" name="delete" value="Delete Key"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index" id="${account.getLogin().getUserName()}"><g:message code="general.returnToOptions" /></g:link>
</div>
</g:form>
