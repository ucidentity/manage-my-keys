dataSource {
    pooled = false
    driverClassName = "org.postgresql.Driver"
    username = ""
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource_wpa {
            //
            dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:postgresql://localhost/wpa_dev"
            //url = "jdbc:postgresql://alps.ist.berkeley.edu:5300/airbears2?ssl=true"
            driverClassName = "org.postgresql.Driver"
            dialect = 'org.hibernate.dialect.PostgreSQLDialect'
            username = 'wpa_user'
            password = ''
        }
        
        dataSource_calmail {
            dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/calmail2"
            //url = "jdbc:mysql://cm02adm.ist.berkeley.edu:3306/calmail2?useSSL=true"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
            username = 'calnetoken'
            password = ''
        }

    }
    test {
        dataSource_wpa {
            dbCreate = "update"
            url = "jdbc:h2:mem:testWpaDb;MVCC=TRUE"
        }

        dataSource_calmail {
            dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/calmail2"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
            username = 'calnetoken'
            password = ''
        }
    }
    production {
        dataSource_wpa {
            //
            dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
            //url = "jdbc:postgresql://cascades.ist.berkeley.edu:5300/airbears2?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
            url = "jdbc:postgresql://alps.ist.berkeley.edu:5300/airbears2?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
            driverClassName = "org.postgresql.Driver"
            dialect = 'org.hibernate.dialect.PostgreSQLDialect'
            username = 'token_app'
            password = ''
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
        
        dataSource_calmail {
            dbCreate = "validate" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://cmdev01ws.ist.berkeley.edu:3306/calmail2?useSSL=true&verifyServerCertificate=false"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
            username = 'calnetoken'
            password = ''
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
        /*
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE"
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
        */
    }
}
