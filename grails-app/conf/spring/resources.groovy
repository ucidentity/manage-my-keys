import edu.berkeley.calnet.mmk.dev.GoogleAdminAPIDevMockService
import grails.util.Environment

// Place your Spring DSL code here
beans = {
    if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST] ) {
        googleAdminAPIService(GoogleAdminAPIDevMockService)
    }
}
