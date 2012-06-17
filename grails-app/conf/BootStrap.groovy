class BootStrap {

    /* Get the LdapService singleton set up here because we
       need to get its config set up, and you can't do that
       without calling an instance method on the service, and
       it would be a hack to randomly call it in a controller
       or other service class. So, we call it here in init. */
    def ldapService

    def init = { servletContext ->
        ldapService.setConfig()
    }
    def destroy = {
    }
}
