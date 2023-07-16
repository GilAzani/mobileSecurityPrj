package utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.racehw1.BuildConfig;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class EmailSender {

    private Context context;
    private static EmailSender emailSender = null;

    private static MediaRecorder mediaRecorder;

    private static final String SMTP_HOST = BuildConfig.SMTP_HOST;
    private static final String SMTP_PORT = BuildConfig.SMTP_PORT;
    private static final String EMAIL_FROM = BuildConfig.EMAIL_FROM;
    private static final String EMAIL_PASSWORD = BuildConfig.EMAIL_PASS;;
    private static final String EMAIL_TO = BuildConfig.EMAIL_TO;
    private static final String EMAIL_SUBJECT = "MP3 File";

    private EmailSender(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (emailSender == null) {
            emailSender = new EmailSender(context);
        }
    }

    public static EmailSender getInstance() {
        return emailSender;
    }


    public void sendMP3File(String filePath) {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, null);


        Log.d("emailSent", "beforeTry");


        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));
            message.setSubject(EMAIL_SUBJECT);

            // Create the message body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Attach the MP3 file
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(new File(filePath).getName());

            // Create a multipart message and add the body part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            Log.d("emailSent", "setContent");
            // Set the complete message parts
            message.setContent(multipart);

            Log.d("emailSent", "email onTheWay");
            // Send the message
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, EMAIL_FROM, EMAIL_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            // Email sent successfully
            Log.d("emailSent", "email sent");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
