package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils extends Object {
    private static final long serialVersionUID = 1L;

    private static final String MAIL_HOST_INCOMING = "mailfwd.nih.gov";

    public static String[] getRecipients() throws Exception {
        String ncicb_contact_url = NCItBrowserProperties.getProperty(
            NCItBrowserProperties.NCICB_CONTACT_URL);
        return Utils.toStrings(ncicb_contact_url, ";", false);
    }

    public static void postMail(String from, String recipients[],
            String subject, String message) throws MessagingException {
        // Sets the host smtp address.
        Properties props = new Properties();
        props.put("mail.smtp.host", MAIL_HOST_INCOMING);

        // Creates some properties and get the default session.
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        // Creates a message.
        Message msg = new MimeMessage(session);

        // Sets the from and to addresses.
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);
        msg.setRecipient(Message.RecipientType.BCC, addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++)
            addressTo[i] = new InternetAddress(recipients[i]);
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional: You can set your custom headers in the email if you want.
        msg.addHeader("MyHeaderName", "myHeaderValue");

        // Setting the Subject and Content Type.
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
}
