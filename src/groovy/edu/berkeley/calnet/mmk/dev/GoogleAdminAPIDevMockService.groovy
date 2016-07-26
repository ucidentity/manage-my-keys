package edu.berkeley.calnet.mmk.dev

import com.google.api.services.admin.directory.model.User
import com.google.api.services.admin.directory.model.UserName
import org.springframework.beans.factory.InitializingBean

class GoogleAdminAPIDevMockService implements InitializingBean{
    List<User> users = []

    void updatePasswordToken(String userId, String token) {
        println "Update $userId with token: $token"

    }

    User getUser(String userId) {
        if(!userId) {
            println "Did not create dummy Google User, since userId was null"
            return null
        }
        def user = new User().setPrimaryEmail(userId).setName(new UserName().setGivenName("Test").setFamilyName("User").setFullName("Test X User"))
        println "Create dummy Google User: $user"
        return user
    }

    @Override
    void afterPropertiesSet() throws Exception {
        println "****** Using Dummy for Google API *******"
    }
}