package edu.berkeley.ims.myt

class MmkTagLib {
    static namespace = "mmk"


    def ifBconnected = { attrs, body ->
        if (session.googleAppsAccount) {
            out << body()
        }
    }

}
