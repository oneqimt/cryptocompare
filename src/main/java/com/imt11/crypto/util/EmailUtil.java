package com.imt11.crypto.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Dennis Miller
 *
 * https://www.codejava.net/coding/how-to-implement-forgot-password-feature-for-java-web-application
 */
public class EmailUtil {

    public static void sendEmail(String message, String recipientEmail) throws UnsupportedEncodingException, MessagingException {

        String host = SecurityUtil.getInstance().getSmtpHost();
        String port = SecurityUtil.getInstance().getSmtpPort();
        String password = SecurityUtil.getInstance().getSmtpPassword();
        String senderEmail = SecurityUtil.getInstance().getSmtpSenderEmail();
        String senderName = "Crypto Portfolio";
        String subject = "Your temporary password";

        //String message = "Please use this password to login. We recommend that you change it.";
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        };

        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(senderEmail, senderName));
        InternetAddress[] toAddresses = { new InternetAddress(recipientEmail) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(message);

        // sends the e-mail
        Transport.send(msg);


    }

    public static String getRandomPassword(){

        return RandomStringUtils.randomAlphanumeric(6);
    }
}
