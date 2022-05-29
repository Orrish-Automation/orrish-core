package com.orrish.automation.utility;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;

public class EMailUtility {

    private static Folder inbox = null;
    private static Store store = null;

    // Reference : https://javahowtos.com/guides/127-libraries/366-how-to-read-gmail-using-java-and-javax-mail.html
    // Tried with dependency com.sun.mail:javax.mail:1.6.2
    public static Map getNewGmailMessageForUsernameAndPassword(String username, String password) {
        if (!conditionalStep) return new HashMap();
        try {
            populateEmailConnection("imap.gmail.com", username, password);
            //Wait for new message to arrive.
            Message message = inbox.getMessage(inbox.getMessages().length);
            for (int i = 0; i < SetUp.newEmailWaitTimeoutInSeconds && (inbox.getMessages().length == 0 || isMessageOld(message)); i++) {
                waitSeconds(1);
                message = inbox.getMessage(inbox.getMessages().length);
            }
            //New email did not arrive within timeout.
            if (isMessageOld(message)) {
                ReportUtility.reportInfo("New message did not arrive within " + SetUp.newEmailWaitTimeoutInSeconds + " seconds.");
                return new HashMap<>();
            }
            if (SetUp.emailPostReadAction.trim().equalsIgnoreCase("READ"))
                message.setFlag(Flags.Flag.SEEN, true); //Mark as read
            if (SetUp.emailPostReadAction.trim().equalsIgnoreCase("DELETE"))
                message.setFlag(Flags.Flag.DELETED, true); //Delete mail

            Map<String, String> valueToReturn = populateEmailObject(message);
            ReportUtility.reportPass("Email read: " + valueToReturn);
            return valueToReturn;
        } catch (MessagingException | IOException e) {
            ReportUtility.reportExceptionFail(e);
            return new HashMap();
        } finally {
            closeMailConnection();
        }

    }

    public static boolean sendEmailToWithSubjectWithBodyForUsernameAndPassword(String to, String subject, String body, String user, String password) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            ReportUtility.reportPass("Email sent successfully.");
            return true;
        } catch (MessagingException e) {
            ReportUtility.reportExceptionFail(e);
        }
        return false;
    }

    private static Map<String, String> populateEmailObject(Message message) throws MessagingException, IOException {
        Map<String, String> valueToReturn = new HashMap<>();
        valueToReturn.put("Subject", message.getSubject());
        valueToReturn.put("ReceivedDate", message.getReceivedDate().toString());
        valueToReturn.put("From", message.getFrom()[0].toString());
        valueToReturn.put("Body", getTextFromMessage(message));
        return valueToReturn;
    }

    public static Map getFirstGmailMessageForUsernameAndPassword(String username, String password) {
        if (!conditionalStep) return new HashMap();
        try {
            populateEmailConnection("imap.gmail.com", username, password);
            Message[] messages = inbox.getMessages();
            if (messages.length == 0) return new HashMap();
            Message message = inbox.getMessage(inbox.getMessages().length);
            Map<String, String> valueToReturn = populateEmailObject(message);
            ReportUtility.reportPass("Email read: " + valueToReturn);
            return valueToReturn;
        } catch (MessagingException | IOException e) {
            ReportUtility.reportExceptionFail(e);
            return new HashMap();
        } finally {
            closeMailConnection();
        }
    }

    public static boolean deleteAllEmailsForUsernameAndPassword(String username, String password) {
        if (!conditionalStep) return true;
        try {
            populateEmailConnection("imap.gmail.com", username, password);
            //Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            Message[] messages = inbox.getMessages();
            return deleteAllMessages(messages);
        } catch (MessagingException e) {
            ReportUtility.reportExceptionFail(e);
        } finally {
            closeMailConnection();
        }
        return false;
    }

    private static void populateEmailConnection(String host, String username, String password) throws MessagingException {

        Properties properties = new Properties();
        //properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.trust", host);

        /*
        properties.put("mail.store.protocol", "pop3");
         properties.put("mail.pop3s.host", pop3Host);
         properties.put("mail.pop3s.port", "995");
         properties.put("mail.pop3.starttls.enable", "true");
         */

        getEmailConnection(host, username, password, properties);

    }

    private static void getEmailConnection(String host, String username, String password, Properties properties) throws MessagingException {
        Session emailSession = Session.getDefaultInstance(properties);
        store = emailSession.getStore("imaps");
        //Store store = emailSession.getStore("pop3s");
        store.connect(host, username, password);
        inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_WRITE);
    }

    private static void closeMailConnection() {
        try {
            if (inbox != null && inbox.isOpen())
                inbox.close(false);
            if (store != null && store.isConnected())
                store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteAllMessages(Message[] messages) throws MessagingException {
        for (int i = messages.length - 1; i >= 0; i--)
            messages[i].setFlag(Flags.Flag.DELETED, true); //Delete mail
        ReportUtility.reportPass("Email deleted: " + messages.length);
        return true;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    private static boolean isMessageOld(Message message) throws MessagingException {
        return message.getFlags().contains(Flags.Flag.SEEN) || !message.getReceivedDate().after(new Date(System.currentTimeMillis() - SetUp.newEmailWaitTimeoutInSeconds * 1000));
    }

}
