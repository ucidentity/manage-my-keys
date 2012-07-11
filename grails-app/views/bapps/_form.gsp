
<g:form action="save" id="${account.getLogin().getUserName()}">
<g:hiddenField name="token" value="${token}" />
<fieldset>
    <legend><g:message code="bapps.setPage.legend" /></legend>

<div class="tabbable">
    <ul class="nav nav-tabs">
      <li class="active"><a href="#pre" data-toggle="tab" title="Pre-Generated token.">Pre-Generated</a></li>
      <li><a href="#defined" data-toggle="tab" id="defineTab" title="Define Your Own token.">Define Your Own</a></li>
    </ul>

    <div class="tab-content">
      
      <div class="tab-pane active" id="pre">    
        <div class="control-group">
          <label class="control-label" for="token">Pre-Generated Key</label>
          <p><span class="token-value">${token}</span></p>
          <p><g:link action="set" id="${account.getLogin().getUserName()}" class="btn" title="Generate a different key."><i class="icon-refresh"></i> <g:message code="bapps.formPage.generateKey" /></g:link>
          <p class="help-block"><g:message code="bapps.formPage.keyHelpText" /></p>
        </div>
      </div>
      
      <div class="tab-pane" id="defined">
        <div class="control-group">
            <p class="${hasErrors(bean:googleApps,field:'definedToken', 'error')}">
            <label class="control-label" for="definedToken">Key</label>
            <input type="password" id="definedToken" name="definedToken">
            </p>
            <p class="${hasErrors(bean:googleApps,field:'definedTokenConfirmation', 'error')}">
            <label class="control-label" for="definedTokenConfirmation">Key Confirmation</label>
            <input type="password" id="definedTokenConfirmation" name="definedTokenConfirmation">
            </p>
            <p class="help-block"><g:message code="bapps.formPage.keyHelpTextForUserDefined" /></p>
        </div>
      </div>
    
    </div> <!-- End tab-content -->
</dig> <!-- End of tabbable -->
    
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" name="save" value="Set Key"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index" id="${account.getLogin().getUserName()}">Cancel</g:link>
    </div>

</fieldset>
</g:form>

<g:if test="${userDefined}">
<script>
$(document).ready(function() {
    $('#defineTab').click();
    $("#definedToken").focus();
});
</script>
</g:if>