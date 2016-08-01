package edu.berkeley.ims.myt

class MmkTagLib {
    static namespace = "mmk"


    def ifBconnected = { attrs, body ->
        boolean googleAppsAccount = session.googleAppsAccount
        if (googleAppsAccount) {
            out << body()
        }
    }

}
