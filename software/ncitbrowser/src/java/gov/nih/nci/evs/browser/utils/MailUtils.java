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

    public static String getProperty(String property, String propertyName)
            throws Exception {
        String value = null;
        try {
            value = NCItBrowserProperties.getProperty(property);
        } catch (Exception e) {
            throw new Exception("Error reading \"" + propertyName
                    + "\" property.");
        }
        return value;
    }

    public static String[] getRecipients() throws Exception {
        String value = getProperty(NCItBrowserProperties.NCICB_CONTACT_URL,
            "ncicb.contact.url");
        return Utils.toStrings(value, ";", false);
    }

    public static String getIncomingMailHost() throws Exception {
        String value = getProperty(NCItBrowserProperties.INCOMING_MAIL_HOST,
            "incoming.mail.host");
        return value;
    }

    public static void postMail(String from, String recipients[],
            String subject, String message) throws MessagingException,
            Exception {
        StringBuffer error = new StringBuffer();
        String incoming_mail_host = getIncomingMailHost();

        if (incoming_mail_host == null || incoming_mail_host.length() <= 0)
            error.append("mail host not set.\n");
        if (from == null || from.length() <= 0)
            error.append("from field not set.\n");
        if (subject == null || subject.length() <= 0)
            error.append("subject field not set.\n");
        if (message == null || message.length() <= 0)
            error.append("message field not set.\n");
        if (error.length() > 0)
            throw new Exception(error.toString());

        // Sets the host smtp address.
        Properties props = new Properties();
        props.put("mail.smtp.host", incoming_mail_host);

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
