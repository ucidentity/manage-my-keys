import edu.berkeley.calnet.mmk.dev.GoogleAdminAPIDevMockService

// Place your Spring DSL code here
beans = {
    environments {
        test {
            googleAdminAPIService(GoogleAdminAPIDevMockService)
        }
    }
}
