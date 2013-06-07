import edu.berkeley.ims.myt.CalMail

class BootStrap {

    /* Get the LdapService singleton set up here because we
       need to get its config set up, and you can't do that
       without calling an instance method on the service, and
       it would be a hack to randomly call it in a controller
       or other service class. So, we call it here in init. */
    def ldapService

    def init = { servletContext ->
        ldapService.setConfig()
        
        // Set up the calmail database rows.
        environments {
            development {
                //createAccounts()
            }
            test {
                //createAccounts()
            }
        }
    }
    def destroy = {
    }
    
    private void createAccounts() {
        CalMail.withTransaction { status ->
            [
                [7729, 'lucas.rockwell', 110, 'google', 'abc'],
                [7729, 'calnet-test', 110, 'google', 'abc'],
                [7729, 'calnet-test1', 110, 'google', 'abc'],
                [7729, 'calnet-test2', 110, 'google', 'abc'],
                [7729, 'calnet-test3', 110, 'google', 'abc'],
                [7729, 'calnet-test4', 110, 'google', 'abc'],
                [7729, 'tokenapp', 110, 'google', 'abc']
            ].each { ownerUid, localpart, domainId, host, deptCode ->
                def account = new CalMail(
                    ownerUid: ownerUid, 
                    localpart: localpart, 
                    domainId: domainId, 
                    host: host, 
                    deptCode: deptCode
                ).save()
            }
        }
    }
    
}
