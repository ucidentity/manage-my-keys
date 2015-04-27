import javax.mail.*

Properties props = System.getProperties()
props.setProperty("mail.store.protocol", "imaps")
Session session = Session.getDefaultInstance(props, null)
Store store = session.getStore("imaps")
store.connect("imap.gmail.com", "calnet-tv@berkeley.edu", '<password>')
Folder inbox = store.getFolder("INBOX")
inbox.open(Folder.READ_ONLY)
println inbox.messageCount
