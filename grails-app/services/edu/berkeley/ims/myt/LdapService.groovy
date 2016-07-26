package edu.berkeley.ims.myt
import com.unboundid.ldap.sdk.*
import com.unboundid.util.ssl.SSLUtil
import com.unboundid.util.ssl.TrustAllTrustManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.InitializingBean

import java.security.GeneralSecurityException

class LdapService implements InitializingBean {
    
    Logger log = Logger.getLogger(LdapService.class);
    
    def grailsApplication
    
    String peopleDn
    String guestsDn
    String peopleRdnAttr
    String departmentsDn
    String personUsernameAttr
    String personIdAttr
    String groupMemberAttr = 'isMemberOf'
    String authZGroupDn
    String baseGroupDn
    String groupRdnAttr
    
    Boolean alwaysTrustSslCert = true
    Boolean useSsl = true
    Boolean autoReconnect = true
    Boolean followReferrals = true
    
    FailoverServerSet serverSet = null
    
    public LdapService() {
        
    }
    
    def setConfig() {
        autoReconnect      = grailsApplication.config.ldap.autoReconnect
        peopleDn           = grailsApplication.config.ldap.peopleDn
        guestsDn           = grailsApplication.config.ldap.guestsDn
        peopleRdnAttr      = grailsApplication.config.ldap.peopleRdnAttr
        departmentsDn      = grailsApplication.config.ldap.departmentsDn
        personUsernameAttr = grailsApplication.config.ldap.personUsernameAttr
        personIdAttr       = grailsApplication.config.ldap.personIdAttr
        groupMemberAttr    = grailsApplication.config.ldap.groupMemberAttr
        authZGroupDn       = grailsApplication.config.ldap.authZGroupDn
        baseGroupDn        = grailsApplication.config.ldap.baseGroupDn
        groupRdnAttr       = grailsApplication.config.ldap.groupRdnAttr
        alwaysTrustSslCert = grailsApplication.config.ldap.alwaysTrustSslCert
        useSsl             = grailsApplication.config.ldap.useSsl
    }
    
    /**
     * Removes all instances of ( and/or ) as parens will mess up an LDAP
     * filter.
     *
     * @param {@code string} - the string to clean
     * @return String - the {@code string} with all instances of (
     * or ) removed
     */
    def cleanString(String string) {
        if (string == null) {
            return string
        }
        return string.replaceAll("[\\(\\)]+", "")
    }
    
    def find(final String attributeName, final String value) {
        List<SearchResultEntry> entries = search(attributeName, cleanString(value), peopleDn)
        if (entries.size() > 0) {
            return entries[0]
        } else if(guestsDn) {
            entries = search(attributeName, cleanString(value), guestsDn)
            return entries.size() > 0 ? entries[0] : null
        }
        return null
    }
    
    /**
     * Finds a person in LDAP given the passed in {@code email} address.
     * It returns the first person that is returned if more than one person
     * is returned. It does this because no two people should have the same
     * email address.
     *
     * @param {@code email} - the email to use in the mail= filter
     * @return SearchResultEntry - the person, or null if no one was found
     */
    def findByEmail(final String email) {
        List<SearchResultEntry> entries = search("mail", cleanString(email), peopleDn)
        if (entries.size() > 0) {
            return (SearchResultEntry)entries.get(0)
        }
        return null
    }
    
     /**
     * Finds a person in LDAP given the passed in {@code id}.
     * It returns the first person that is returned if more than one person
     * is returned. Uses {@code personIdAttr} as the attribute in the filter.
     *
     * @param {@code id} - the ID to use in the filter
     * filter
     * @return SearchResultEntry - the person, or null if no one was found
     */
    def findById(final String id) {
        List<SearchResultEntry> entries = search(personIdAttr, id, peopleDn)
        if (entries.size() > 0) {
            return (SearchResultEntry)entries.get(0)
        }
        return null
    }
    
    /**
     * Finds a person in LDAP given the passed in {@code eppn}.
     * It returns the first person that is returned if more than one person
     * is returned. It does this because no two people should have the same
     * EPPN.
     *
     * @param {@code eppn} - the EPPN to use in the eduPersonAffiliation=
     * filter
     * @return SearchResultEntry - the person, or null if no one was found
     */
    def findByEppn(final String eppn) {
        List<SearchResultEntry> entries = search("eduPersonPrincipalName", eppn, peopleDn)
        if (entries.size() > 0) {
            return (SearchResultEntry)entries.get(0)
        }
        return null
    }
    
    /**
     * Finds a person in LDAP given the passed in {@code uid}.
     * It returns the first person that is returned if more than one person
     * is returned. It does this because no two people should have the same
     * EPPN.
     *
     * @param {@code uid} - the UID to use in the uid= filter
     * @return SearchResultEntry - the person, or null if no one was found
     */
    def findByUid(final String uid) {
        List<SearchResultEntry> entries = search("uid", uid, peopleDn)
        if (entries.size() > 0) {
            return (SearchResultEntry)entries.get(0)
        }
        return null
    }

    /**
     * Finds a person in LDAP given the passed in {@code dn}.
     * It returns the first person that is returned if more than one person
     * is returned. It does this because no two people should have the same
     * EPPN.
     *
     * @param {@code dn} - the DN to use for searching
     * @return SearchResultEntry - the person, or null if no one was found
     */
    def findByDn(final String dn) {
        try {
            SearchResult results = connection().search(
                dn, SearchScope.BASE, "objectclass=*", "*");
            List<SearchResultEntry> entries = results.getSearchEntries();
            if (entries.size() > 0) {
                return entries.get(0);
            }
            return null;
        }
        catch (LDAPSearchException lse) {
            log.error("Cound not find entry specified by dn: " + dn);
            return null;
        }
    }
    
    def personExists(final String lastName, final String email) {
        def filter = "(&(sn=${cleanString(lastName)}*)(mail=${cleanString(email)}))"
        List<SearchResultEntry> entries = searchUsingFilter(
            filter, peopleDn)
        if (entries.size() > 0) {
            return true
        }
        return false
    }
    
    def isMemberOfAuthZGroup(final String username) {
        return isMemberOfGroup(username, authZGroupDn)
    }
    
    def isMemberOfGroup(final String username, final String groupDn) {
        def filter = "(&(${personUsernameAttr}=${cleanString(username)})(${groupMemberAttr}=${groupDn}))"
        List<SearchResultEntry> entries = searchUsingFilter(filter, peopleDn)
        if (entries.size() == 1) {
            return true
        }
        return false
    }
    
    /**
     * Gets all of the members for the passed in {@code group}.
     * It gets the list from the 'member attribute', and then fetches the actual
     * person (member) entries from ou=people.
     *
     * @param SearchResultEntry - the entry to use for fetching the
     * list of sponsors
     * @return List<SearchResultEntry> - the members for this group.
     */
    def membersForGroupName(final String groupName) {
        return peopleOfTypeForGroupName('member', groupName)
    }
    
    /**
     * Gets all of the owners for the passed in {@code group}.
     * It gets the list from the 'owner' attribute, and then fetches the actual
     * person (owners) entries from ou=people.
     *
     * @param SearchResultEntry - the entry to use for fetching the
     * list of sponsors
     * @return List<SearchResultEntry> - the owners for this group.
     */
    def ownersForGroupName(final String groupName) {
        return peopleOfTypeForGroupName('owner', groupName)
    }
    
    /**
     * Gets all of the {@code type} for the passed in {@code group}.
     * It gets the list from the {@code type} attribute, and then fetches the actual
     * person (sponsor) entries from ou=people.
     *
     * @param SearchResultEntry - the entry to use for fetching the
     * list of sponsors
     * @return List<SearchResultEntry> - the {@code type} for this group.
     */
    def peopleOfTypeForGroupName(final String type, final String groupName) {
        List<SearchResultEntry> entries = search(groupRdnAttr, groupName,
            baseGroupDn)
        if (entries.size() > 0) {
            def entry = entries.get(0)
            def people = new ArrayList()
            entry.getAttributeValues(type).each { dn ->
                def person = findByDn(dn)
                if (person != null) {
                    people.add(person)
                }
            }
            return people
        }
        return null
    }
    
    /**
     * Adds the passed in {@code person} to the passed in {@code groupName}
     * as an 'owner'.
     *
     * @param {@code person} - the person to add
     * @param {@code groupName} - the name of the group to which the person should
     * be added as an 'owner'
     */
    def addOwnerToGroup(final SearchResultEntry person, final String groupName) {
        return addToGroup(person, groupName, 'owner')
    }
    
    /**
     * Adds the passed in {@code person} to the passed in {@code groupName}
     * as a 'member'.
     *
     * @param {@code person} - the person to add
     * @param {@code groupName} - the name of the group to which the person should
     * be added as a 'member'
     */
    def addMemberToGroup(final SearchResultEntry person, final String groupName) {
        return addToGroup(person, groupName, 'member')
    }
    
    /**
     * Adds the passed in {@code person} to the passed in {@code groupName}
     * as a {@code type}.
     *
     * @param {@code person} - the person to add
     * @param {@code groupName} - the name of the group to which the person should
     * be added as {@code type}
     * @param {@code type} - the type of person this is (member, owner, or roleOccupant)
     */
    def addToGroup(final SearchResultEntry person, final String groupName,
        final String type) {
        def people
        if (type.equals('member')) {
            people = membersForGroupName(groupName)
        }
        else if (type.equals('owner')) {
            people = ownersForGroupName(groupName)
        }
        
        // Don't try to add the person if they are already there.
        def inGroup = false
        people.each { member ->
            if (member.getDN().equals(person.getDN())) {
                inGroup = true
            }
        }
        
        if (inGroup == false) {
            try {
                def dn = "${groupRdnAttr}=${groupName}," + baseGroupDn
                ArrayList<Modification> mods = new ArrayList<Modification>();
                mods.add(new Modification(ModificationType.ADD, 
                    type, person.getDN()));
                connection().modify(dn, (List<Modification>)mods);
                return true
            }
            catch (LDAPException le) {
                log.error("exception: " + le.toString());
            }
        }
        else {
            log.info "Person is already a ${type} of group ${groupName}."
            return true
        }
        return false
        
    }
    
    /**
     * Removes the passed in {@code person} from the 'owner' attribute of the
     * passed in {@code groupName}.
     *
     * @param {@code person} - the person to remove
     * @param {@code groupName} - the name of the group to which the person should
     * be removed from the 'owner' attribute
     */
    def removeOwnerFromGroup(final String id, final String groupName) {
        return removeFromGroup(id, groupName, 'owner')
    }
    
    /**
     * Removes the passed in {@code person} from the 'member' attribute of the
     * passed in {@code groupName}.
     *
     * @param {@code person} - the person to remove
     * @param {@code groupName} - the name of the group to which the person should
     * be removed from the 'member' attribute
     */
    def removeMemberFromGroup(final String id, final String groupName) {
        return removeFromGroup(id, groupName, 'member')
    }
    
    /**
     * Removes the passed in {@code person} from the {@code type} attribute of the
     * passed in {@code groupName}.
     *
     * @param {@code person} - the person to remove
     * @param {@code groupName} - the name of the group to which the person should
     * be removed from the {@code type} attribute
     * @param {@code type} - the name of the attribute from which the person should
     * be removed
     */
    def removeFromGroup(final String id, final String groupName, final String type) {
        try {
            def groupdn = "${groupRdnAttr}=${groupName}," + baseGroupDn
            def personDn = "${peopleRdnAttr}=${id}," + peopleDn
            ArrayList<Modification> mods = new ArrayList<Modification>();
            mods.add(new Modification(ModificationType.DELETE, 
                type, personDn));
            connection().modify(groupDn, (List<Modification>)mods);
            return true
        }
        catch (LDAPException le) {
            log.error("exception: " + le.toString());
        }
        return false
    }

    def modAttribute(String attribute, String value, SearchResultEntry person, 
        boolean addToExistingAttributes) {
        def attr = person.getAttributeValues(attribute)
        
        def i = 0
        if (addToExistingAttributes) {
            def hasValue = false
            for (String val: attr) {
                if (val == value) {
                    hasValue = true
                }
            }
            if (hasValue) {
                return attr
            }
            else {
                String[] newAttr = new String[attr.length + 1]
                for (String val: attr) {
                    newAttr[i] = val
                    i++
                }
                newAttr[i] = value
                return newAttr
            }
        }
        else {
            String[] newAttr = new String[attr.length -1]
            for (String val: attr) {
                if (val != value) {
                    newAttr[i] = val;
                    i++;
                }
            }
            return newAttr;
        }
        
    }

    def delete(dn) {
         try {
            connection().delete(dn)
            log.info("Delete successful for: " + dn)
            return true
        }
        catch (LDAPException le) {
            log.error("Delete failed for: " + dn)
            return false
        }
    }

    def setUpLdap() {
        setUpLdap(
            grailsApplication.config.ldap.addresses,
            grailsApplication.config.ldap.ports
        )
    }
    
    def setUpLdap(final String addresses, final String ports) {
        println "addresses: ${addresses}, ports: ${ports}"
        final String[] addressesArray   = addresses.split(",")
        final String[] portsStringArray = ports.split(",")
        
        int[] portsIntArray = new int[portsStringArray.length]
        
        if (portsStringArray.length == 1) {
            portsIntArray[0] = Integer.parseInt(portsStringArray[0])
        }
        else {
            for (int i = 0; i < portsStringArray.length; i++) {
                portsIntArray[i] = Integer.parseInt(portsStringArray[i])
            }
        }
        
        LDAPConnectionOptions options = new LDAPConnectionOptions()
        options.setAutoReconnect(autoReconnect)
        options.setFollowReferrals(followReferrals)
        try {
            if (useSsl) {
                SSLUtil sslUtil;
                if (alwaysTrustSslCert) {
                    sslUtil = new SSLUtil(new TrustAllTrustManager())
                }
                else {
                    sslUtil = new SSLUtil();
                }
                serverSet = new FailoverServerSet(
                    addressesArray, portsIntArray, sslUtil.createSSLSocketFactory(), options)
            }
            else {
                serverSet = new FailoverServerSet(addresses, ports, options)
            }
        }
        catch (GeneralSecurityException gse) {
            log.error "Error connecting via SSL: " + gse.toString()
        }
    }
    
    /**
     * Get a connection from the {@code serverSet} and then bind. Right now
     * it assumes it is going to connect and then it just binds. This should
     * really throw an exception...
     *
     * @return LDAPConnection the authenticated LDAPConnection
     */
    def connection() {
        if (serverSet == null) {
            setUpLdap()
        }
        LDAPConnection conn = serverSet.getConnection()
        conn.bind(grailsApplication.config.ldap.bindDn, grailsApplication.config.ldap.bindPassword)
        return conn
    }
    
    /**
     * Returns a new List<SearchResultEntry> with the results of
     * of the search. Returns an empty LinkedList<SearchResultEntry> if
     * any exceptions are thrown.
     *
     * Note: The search scope is SearchScope.SUBORDINATE_SUBTREE.
     *
     * @param {@code attribute} - attribute to search for
     * @param {@code value} - the value of {@code attribute}
     * @param {@code base} - the search base
     * @return List<SearchResultEntry> - a List of SearchResultEntry objects
     */
    private List<SearchResultEntry> search(final String attribute, 
        final String value, final String base) {
        
        String filter = "(${attribute}=${cleanString(value)})"
        
        log.debug("Filter: ${filter}")
        
        return searchUsingFilter(filter, base)
    }
    
    private List<SearchResultEntry> searchUsingFilter(final String filter, 
        final String base) {
        
        SearchRequest searchRequest = new SearchRequest(
                base, 
                //SearchScope.SUBORDINATE_SUBTREE, // Sun DSEE doesn't have SUBORDINATE_SUBTREE
                SearchScope.SUB,
                filter,
                "*")
                
        LDAPConnection conn = connection()
        
        try {
            SearchResult results = conn.search(searchRequest)
            List<SearchResultEntry> entries = results.getSearchEntries()
            return entries
        }
        catch (LDAPSearchException lse) {
            log.error "Exception while searching: ${lse.getMessage()}"
            return new LinkedList<SearchResultEntry>()
        }
        finally {
            conn.close()
        }
    }
    
    /**
     * Determines if today is between the {@code start} and {@code expires}.
     * It first checks to make sure that {@code start} and {@code expires}
     * are not null.
     *
     * @param {@code start} - the start date
     * @param {@code expires} - the expires date
     * @return boolean - true if start and expires are not null and today is
     * between start and expires, and false otherwise.
     */
    private boolean withinDates(final Date start, final Date expires) {
        Date now = new Date();
        if (expires != null && start != null &&
            start.before(now) && expires.after(now)) {
            return true;
        }
        return false;
    }

    @Override
    void afterPropertiesSet() throws Exception {
        setConfig()
    }
}