package com.orrish.automation.utility;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;

public class EMailUtility {

    // Reference : https://javahowtos.com/guides/127-libraries/366-how-to-read-gmail-using-java-and-javax-mail.html
    // Tried with dependency com.sun.mail:javax.mail:1.6.2
    public static Map getFirstUnreadGmailForUsernameAndPassword(String username, String password) {
        if (!conditionalStep) return new HashMap();
        Object value = actionOnEmail("imap.gmail.com", username, password, false);
        return (value instanceof Map) ? (Map) value : new HashMap();
    }

    public static boolean deleteAllEmailsForUsernameAndPassword(String username, String password) {
        if (!conditionalStep) return true;
        Object returnValue = actionOnEmail("imap.gmail.com", username, password, true);
        return (returnValue instanceof Boolean) ? Boolean.parseBoolean(returnValue.toString()) : false;
    }

    private static Object actionOnEmail(String host, String username, String password, boolean shouldDeleteAll) {

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

        Map<String, String> valueToReturn = new HashMap<>();
        Folder inbox = null;
        Store store = null;
        try {
            Session emailSession = Session.getDefaultInstance(properties);
            store = emailSession.getStore("imaps");
            //Store store = emailSession.getStore("pop3s");
            store.connect(host, username, password);
            inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);

            //Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            Message[] messages = inbox.getMessages();
            if (shouldDeleteAll) {
                for (int i = messages.length - 1; i >= 0; i--)
                    messages[i].setFlag(Flags.Flag.DELETED, true); //Delete mail
                ReportUtility.reportPass("Email deleted: " + messages.length);
                return true;
            }
            //Wait for new message to arrive.
            for (int i = 0; i < SetUp.newEmailWaitTimeoutInSeconds && (messages.length == 0 || messages[messages.length - 1].getFlags().contains(Flags.Flag.SEEN)); i++) {
                waitSeconds(1);
                messages = inbox.getMessages();
            }
            //New email did not arrive within timeout.
            if (messages[messages.length - 1].getFlags().contains(Flags.Flag.SEEN)) {
                ReportUtility.reportInfo("New message did not arrive within " + SetUp.newEmailWaitTimeoutInSeconds + " seconds.");
                return new HashMap<>();
            }
            Message message = messages[messages.length - 1];
            if (SetUp.emailPostReadAction.trim().equalsIgnoreCase("READ"))
                message.setFlag(Flags.Flag.SEEN, true); //Mark as read
            if (SetUp.emailPostReadAction.trim().equalsIgnoreCase("DELETE"))
                message.setFlag(Flags.Flag.DELETED, true); //Delete mail

            valueToReturn.put("Subject", message.getSubject());
            valueToReturn.put("ReceivedDate", message.getReceivedDate().toString());
            valueToReturn.put("From", message.getFrom()[0].toString());
            valueToReturn.put("Body", getTextFromMessage(message));

        } catch (MessagingException | IOException e) {
            ReportUtility.reportFail("Some problem reading e-mail.");
            ReportUtility.reportExceptionDebug(e);
        } finally {
            try {
                if (inbox != null && inbox.isOpen())
                    inbox.close(false);
                if (store != null && store.isConnected())
                    store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        ReportUtility.reportPass("Email read: " + valueToReturn);
        return valueToReturn;
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
}
