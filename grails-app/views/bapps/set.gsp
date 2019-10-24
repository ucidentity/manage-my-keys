<meta name="layout" content="ucb" />

<h1><g:message code="bapps.setPage.heading" args="${[account.emailAddress]}"/></h1>

<g:include controller="alert" action="formErrors" params="['item':googleApps]" />
<g:include controller="alert" action="flashError" />

<div id="form">

    <g:form action="save">
        <g:hiddenField name="userDefined" value="${userDefined ?: false}"/>
        <fieldset>
            <!-- <legend><g:message code="bapps.setPage.legend" /></legend> -->

            <div class="tabbable">
                <ul class="nav nav-tabs">
                    <li class="${userDefined ? '':'active'}"><a href="#predefined" id="predefineTab" data-toggle="tab" title="Pre-Generated token." >Pre-Generated</a></li>
                </ul>

                <div class="tab-content">

                    <div class="tab-pane ${userDefined ? '':'active'}" id="predefined">
                        <div class="control-group">
                            <p><g:message code="bapps.setPage.preGeneratedMessage" args="${[account.emailAddress]}"/></p>

                            <label class="control-label" for="token">Pre-Generated Key</label>
                            <div class="token-value"><p id="tokenText"></p></div>
                            <p><a href="#" id="generateToken" class="btn" title="Generate a different key."><i class="icon-refresh"></i> <g:message code="bapps.formPage.generateKey" /></a>
                            <p class="help-block"><g:message code="bapps.formPage.keyHelpText" /></p>
                        </div>
                    </div>

                    <div class="tab-pane ${userDefined ? 'active':''}" id="defined">
                        <div class="control-group">
                            <p><g:message code="bapps.setPage.defineYourOwnMessage" args="${[account.emailAddress]}"/></p>

                            <p class="help-block"><g:message code="bapps.formPage.keyHelpTextForUserDefined" /></p>

                            <p class="${hasErrors(bean:googleApps,field:'definedToken', 'error')}">
                                <label class="control-label" for="token">Key</label>
                                <input type="password" id="token" name="token" title="Your self-defined key.">
                            </p>
                            <p class="${hasErrors(bean:googleApps,field:'definedTokenConfirmation', 'error')}">
                                <label class="control-label" for="tokenRepeat">Key Confirmation</label>
                                <input type="password" id="tokenRepeat" name="tokenRepeat" title="Your self-defined key confirmation.">
                            </p>
                        </div>
                    </div>

                </div> <!-- End tab-content -->
            </div> <!-- End of tabbable -->

            <div class="form-actions">
                <input type="submit" class="btn btn-primary" name="save" value="Set Key"/> &nbsp;  &nbsp; <g:link controller="bapps" action="index" id="${account.emailAddress}" token=""><g:message code="general.returnToOptions" /></g:link>
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
        $('#defineTab').click(function () {
            // Remove any self-defined values
            $('[name="token"]').val('');
            $('[name="tokenRepeat"]').val('');
            $('[name="userDefined"]').val(true);
        }) ;
        $('#predefineTab').click(function() {
            $('[name="userDefined"]').val(false);
        });
        $('#generateToken').on('click',function() {
            $('#tokenText').html('-');
            $(this).button('disable');
            $.getJSON('${createLink(action: 'generateToken')}',function(data) {
                $('[name="token"]').val(data.token);
                $('[name="tokenRepeat"]').val(data.token);
                $('#tokenText').text(data.token);
                $(this).button('enable');

            })
        });
        if($('#predefined').hasClass('active')) {
            $('#generateToken').click();
        }
    </script>
</div>
