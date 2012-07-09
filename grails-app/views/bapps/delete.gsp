<meta name="layout" content="ucb" />

<h2><g:message code="bapps.delete.heading" args="${[account.getLogin().getUserName()]}" /></h2>
<p><g:message code="bapps.delete.generalMessage" args="${[account.getLogin().getUserName()]}" /></p>

<p>
<div class="alert alert-info">
    <g:message code="bapps.delete.deleteInfo" />
</div>
</p>

<g:include controller="alert" action="flashError" />

<g:form action="delete" id="${account.getLogin().getUserName()}">
<div class="form-actions">
    <input type="submit" class="btn btn-primary" name="delete" value="Delete Token"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index" id="${account.getLogin().getUserName()}">Cancel</g:link>
</div>
</g:form>
