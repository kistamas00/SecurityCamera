package securitycamera.services;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

	private final static Logger LOGGER = Logger
			.getLogger(Email.class.getCanonicalName());

	private final static String USERNAME = "scameraemail@gmail.com";
	private final static String PASSWORD = "securitycamerapass";

	public static void sendEmail(File attachment) {

		String emailAddress = Settings.getSetting(Settings.EMAIL, String.class);

		if (!emailAddress.equals(USERNAME)) {

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME, PASSWORD);
				}
			});

			try {

				MimeMessage message = new MimeMessage(session);

				message.setFrom(new InternetAddress(USERNAME));
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(emailAddress));
				message.setSubject("Motion detected!");

				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(
						"The security camera detected motion. The captured photo has been attached to this email.");

				BodyPart messageBodyPart2 = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment);
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(attachment.getName());

				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart1);
				multipart.addBodyPart(messageBodyPart2);

				message.setContent(multipart);

				Transport.send(message);

				LOGGER.info("Email sent to: " + emailAddress);

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}

		} else {
			LOGGER.warning("E-mail address didn't set up yet!");
		}
	}
}
