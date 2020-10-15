package com.auo.juppy.result;

import com.auo.juppy.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.net.URI;
import java.util.Properties;

public interface Reporter {
    void notify(RunnerResult result, URI uri);

    class EmailReporter implements Reporter {
        private final Properties properties;
        private final boolean auth;
        private final String username;
        private final String password;
        private final InternetAddress from;
        private final InternetAddress[] to;

        private static final Logger LOGGER = LoggerFactory.getLogger(EmailReporter.class);

        public EmailReporter(Properties properties) throws AddressException {
            this.properties = properties;
            this.auth = Boolean.parseBoolean(properties.getProperty("mail.smtp.auth", "false"));

            this.username = (String) properties.remove(Config.MAIL_AUTH_USERNAME);
            this.password = (String) properties.remove(Config.MAIL_AUTH_PASSWORD);

            if (auth && (username == null || password == null)) {
                //TODO: change this to a proper exception...
                throw new RuntimeException("Password and username need to be specified with auth");
            }

            //TODO: validate combination of items here.

            String mailFrom = (String) properties.remove(Config.MAIL_FROM);
            String mailTo = (String) properties.remove(Config.MAIL_TO);


            this.from = new InternetAddress(mailFrom);
            this.to = InternetAddress.parse(mailTo);
        }

        @Override
        public void notify(RunnerResult result, URI uri) {
            try {
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (!auth) {
                            return null;
                        }

                        if (username == null || password == null) {
                            return null;
                        }

                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(from);
                message.setRecipients(Message.RecipientType.TO, to);

                message.setSubject("Host unreachable.. 😭");
                String msg = "Could not ping url: " + uri.toString();

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(msg, "text/html");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);
                message.setContent(multipart);

                Transport.send(message);
            } catch (MessagingException e) {
                LOGGER.warn("Failed to send email", e);
            }
        }
    }
}
