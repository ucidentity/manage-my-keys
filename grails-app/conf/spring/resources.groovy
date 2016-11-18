import edu.berkeley.calnet.mmk.dev.GoogleAdminAPIDevMockService
import grails.util.Environment

// Place your Spring DSL code here
beans = {
    if (application.config.mmk.googleAdminAPI.useMock) {
        googleAdminAPIService(GoogleAdminAPIDevMockService)
    }
}
