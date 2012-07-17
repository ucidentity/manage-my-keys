
<g:form action="save" id="${account.getLogin().getUserName()}">
<g:hiddenField name="token" value="${token}" />
<fieldset>
    <!-- <legend><g:message code="bapps.setPage.legend" /></legend> -->

<div class="tabbable">
    <ul class="nav nav-tabs">
      <li class="active"><a href="#pre" data-toggle="tab" title="Pre-Generated token." id="preTab">Pre-Generated</a></li>
      <li><a href="#defined" data-toggle="tab" id="defineTab" title="Define Your Own token.">Define Your Own</a></li>
    </ul>

    <div class="tab-content">
      
      <div class="tab-pane active" id="pre">    
        <div class="control-group">
          <p><g:message code="bapps.setPage.preGeneratedMessage" args="${[account.getLogin().getUserName()]}"/></p>
          
          <label class="control-label" for="token">Pre-Generated Key</label>
          <div class="token-value"><p>${token}</p></div>
          <p><g:link action="set" id="${account.getLogin().getUserName()}" class="btn" title="Generate a different key."><i class="icon-refresh"></i> <g:message code="bapps.formPage.generateKey" /></g:link>
          <p class="help-block"><g:message code="bapps.formPage.keyHelpText" /></p>
        </div>
      </div>
      
      <div class="tab-pane" id="defined">
        <div class="control-group">
            <p><g:message code="bapps.setPage.defineYourOwnMessage" args="${[account.getLogin().getUserName()]}"/></p>
            
            <p class="help-block"><g:message code="bapps.formPage.keyHelpTextForUserDefined" /></p>
            
            <p class="${hasErrors(bean:googleApps,field:'definedToken', 'error')}">
            <label class="control-label" for="definedToken">Key</label>
            <input type="password" id="definedToken" name="definedToken" title="Your self-defined key.">
            </p>
            <p class="${hasErrors(bean:googleApps,field:'definedTokenConfirmation', 'error')}">
            <label class="control-label" for="definedTokenConfirmation">Key Confirmation</label>
            <input type="password" id="definedTokenConfirmation" name="definedTokenConfirmation" title="Your self-defined key confirmation.">
            </p>
        </div>
      </div>
    
    </div> <!-- End tab-content -->
</dig> <!-- End of tabbable -->
    
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" name="save" value="Set Key"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index" id="${account.getLogin().getUserName()}"><g:message code="general.returnToOptions" /></g:link>
    </div>

</fieldset>
</g:form>

<div class="modal hide" id="passphraseRequirements">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>CalNet Passphrase Requirements</h3>
      </div>
      <div class="modal-body">
          <g:message code="general.calNetPassphraseRequirements" />
      </div>
      <div class="modal-footer">
        <a href="#" class="btn btn-primary" data-dismiss="modal">Close</a>
      </div>
</div>

<script>
  $('#preTab').click(function (e) {
      // Remove any self-defined values
      $('#definedToken').val('');
      $('#definedTokenConfirmation').val('');
  })
</script>

<g:if test="${userDefined}">
<script>
$(document).ready(function() {
    $('#defineTab').click();
    $("#definedToken").focus();
});
</script>
</g:if>