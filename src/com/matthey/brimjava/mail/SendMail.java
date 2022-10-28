package com.matthey.brimjava.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.matthey.brimjava.mail.util.AddressStructure;

public class SendMail {
	/*public static void send(String to, String subject, String html) {  
		System.setProperty("java.net.preferIPv4Stack", "true");
		Properties properties = System.getProperties();	
		properties.setProperty("mail.smtp.host", "172.30.4.15");	
		Session session = Session.getDefaultInstance(properties);
		try {
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress("brimweb@matthey.com"));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    message.setSubject(subject);
		    message.setContent(html,"text/html");
		    Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}*/
	public static void send(AddressStructure addresses, String subject, String html) {  
		try {
			Properties properties = System.getProperties();	
			properties.setProperty("mail.smtp.host", "172.30.4.15");	
			Session session = Session.getDefaultInstance(properties);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("brimweb@matthey.com"));
		    for (Message.RecipientType recipientType : addresses.getAddresses().keySet()) {
		    	for (String eMail : addresses.getAddresses().get(recipientType)) {
		    		message.addRecipients(recipientType, eMail);
		    	}
		    }
		    message.setSubject(subject);
		    message.setContent(html,"text/html");
		    Transport.send(message);
		    System.out.println("SENDING EMAIL");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}