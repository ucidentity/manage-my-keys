package edu.berkeley.ims.myt

class CalMailService {

    /**
     * Finds the CalMail account by the provided person and username.
     */
    def account(person, username) {
        def uid = person.getAttributeValueAsInteger('uid')
        def account = CalMail.findWhere(ownerUid: uid, localpart: username)
        return account
    }
}
