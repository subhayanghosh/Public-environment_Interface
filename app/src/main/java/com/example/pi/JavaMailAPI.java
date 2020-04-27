package com.example.pi;

import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.UniversalTimeScale;
import android.os.AsyncTask;
import android.widget.Toast;
import java.util.Properties;

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

public class JavaMailAPI extends AsyncTask<Void,Void,Void>  {


    //Variables
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String imagePath;
    private String mMessage;
    private String subject;

    private ProgressDialog mProgressDialog;

    //Constructor
    public JavaMailAPI(Context mContext, String mEmail, String subject, String imagePath, String mMessage) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.imagePath = imagePath;
        this.mMessage = mMessage;
        this.subject = subject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        mProgressDialog = ProgressDialog.show(mContext,"Sending message", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        mProgressDialog.dismiss();

        //Show success toast
        Toast.makeText(mContext,"Message Sent",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress(Utils.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            //mm.setText(mMessage);

            Multipart  emailContent = new MimeMultipart();

            MimeBodyPart text = new MimeBodyPart();
            text.setText(mMessage);

            MimeBodyPart image = new MimeBodyPart();
            try {
                image.attachFile(imagePath);
            }catch(Exception e){
                e.printStackTrace();
            }

            emailContent.addBodyPart(text);
            emailContent.addBodyPart(image);

            //Sending email
            mm.setContent(emailContent);

            Transport.send(mm);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}