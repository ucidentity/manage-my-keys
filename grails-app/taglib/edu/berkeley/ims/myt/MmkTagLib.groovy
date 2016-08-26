package edu.berkeley.ims.myt

class MmkTagLib {
    static namespace = "mmk"


    def ifBconnected = { attrs, body ->
        boolean googleAppsAccount = session.googleAppsAccount
        if (googleAppsAccount) {
            out << body()
        }
    }
    def ifWpa = { attrs, body ->
        boolean showWpa = session.isWpaAuthorized
        if (showWpa) {
            out << body()
        }
    }

    def isNotWpaOrBconnected = { attrs, body ->
        boolean neither = !session.isWpaAuthorized && !session.googleAppsAccount
        if (neither) {
            out << body()
        }
    }
}
