package org.wocommunity.cookbook.examples.components;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.SharedByteArrayInputStream;

import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.MultipartReport;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponent;
import er.extensions.net.ERXEmailValidator;
import er.javamail.ERIMAP;
import er.javamail.ERJavaMail;
import er.javamail.ERMailAttachment;
import er.javamail.ERMailDeliveryHTML;
import er.javamail.ERMailDeliveryPlainText;
import er.javamail.ERMailFileAttachment;
import er.javamail.ERMessage;

public class CBEmailExamples extends ERXComponent {

  private String _emailFrom;
  private String _emailTo;
  private String _message;

  public CBEmailExamples(WOContext context) {
    super(context);
  }

  public void setEmailFrom(String emailFrom) {
    this._emailFrom = emailFrom;
  }

  public String emailFrom() {
    return this._emailFrom;
  }
  
  public void setEmailTo(String emailTo) {
    this._emailTo = emailTo;
  }

  public String emailTo() {
    return this._emailTo;
  }
  
  public void setMessage(String message) {
    this._message = message;
  }

  public String message() {
    return this._message;
  }

  public WOActionResults sendPlainTextEmail() {
    ERMailDeliveryPlainText mail = new ERMailDeliveryPlainText();

    try {
      mail.newMail ();
      mail.setFromAddress(_emailFrom);
      mail.setReplyToAddress(_emailFrom);
      mail.setSubject("Plain text from ERJavaMail");
      mail.setToAddresses(new NSArray<String> (_emailTo));
      mail.setTextContent("Content of the email");

      mail.sendMail ();
      
      _message = "Success";
    } catch (Exception e) {
      _message = e.getMessage();
    }
    return null;
  }

  public WOActionResults sendHTMLEmail() {
    ERMailDeliveryHTML mail = new ERMailDeliveryHTML();

    try {
      mail.newMail ();
      mail.setFromAddress(_emailFrom);
      mail.setReplyToAddress(_emailFrom);
      mail.setSubject("HTML from ERJavaMail");
      mail.setToAddresses(new NSArray<String> (_emailTo));
      mail.setHTMLContent("<html><head></head><body>HTML <strong>content</strong></body></html>");
      mail.setHiddenPlainTextContent("Content of the email");

      mail.sendMail ();

      _message = "Success";
    } catch (Exception e) {
      _message = e.getMessage();
    }
    return null;
  }

  public WOActionResults sendAttachementEmail() {
    ERMailDeliveryHTML mail = new ERMailDeliveryHTML();

    try {
      mail.newMail ();
      mail.setFromAddress(_emailFrom);
      mail.setReplyToAddress(_emailFrom);
      mail.setSubject("HTML from ERJavaMail");
      mail.setToAddresses(new NSArray<String> (_emailTo));
      mail.setComponent(pageWithName(CBHTMLEmail.class));
      mail.setAlternativeComponent(pageWithName(CBPlainTextEmail.class));
      
      URL logoURL = application().resourceManager().pathURLForResourceNamed("wonder.png", null, null);
      File logo = new File(logoURL.toURI());
      ERMailAttachment imageLogo = new ERMailFileAttachment("wonder.png","<image0>",logo);
      mail.addAttachment(imageLogo);
      
      mail.sendMail ();

      _message = "Success";
    } catch (Exception e) {
      _message = e.getMessage();
    }
    return null;
  }
  
  public WOActionResults sendComponentEmail() {
    ERMailDeliveryHTML mail = new ERMailDeliveryHTML();

    try {
      mail.newMail ();
      mail.setFromAddress(_emailFrom);
      mail.setReplyToAddress(_emailFrom);
      mail.setSubject("HTML from ERJavaMail with attachements");
      mail.setToAddresses(new NSArray<String> (_emailTo));
      mail.setComponent(pageWithName(CBHTMLEmail.class));
      mail.setAlternativeComponent(pageWithName(CBPlainTextEmail.class));
            
      mail.sendMail ();

      _message = "Success";
    } catch (Exception e) {
      _message = e.getMessage();
    }
    return null;
  }
  
  public WOActionResults sendInlineImagesEmail() {
    ERMailDeliveryHTML mail = new ERMailDeliveryHTML();

    try {
      mail.newMail ();
      mail.setFromAddress(_emailFrom);
      mail.setReplyToAddress(_emailFrom);
      mail.setSubject("HTML from ERJavaMail with inline images");
      mail.setToAddresses(new NSArray<String> (_emailTo));
      mail.setComponent(pageWithName(CBInlineImagesEmail.class));
      mail.setAlternativeComponent(pageWithName(CBPlainTextEmail.class));
      
      URL logoURL = application().resourceManager().pathURLForResourceNamed("wonder.png", null, null);
      File logo = new File(logoURL.toURI());
      ERMailAttachment imageLogo = new ERMailFileAttachment("wonder.png","<image0>",logo);
      mail.addInlineAttachment(imageLogo);
      
      mail.sendMail ();

      _message = "Success";
    } catch (Exception e) {
      _message = e.getMessage();
    }
    return null;
  }
  
  public WOActionResults checkBounces() {
    try {
      ERIMAP imapConnection = new ERIMAP();
      imapConnection.openConnection("username", "password");
      com.sun.mail.imap.IMAPFolder inbox;
      inbox = imapConnection.openFolder("INBOX", com.sun.mail.imap.IMAPFolder.READ_ONLY);
      NSArray<Message> messages = new NSArray<Message>(inbox.getMessages(inbox.getMessageCount() - 2, inbox.getMessageCount()));
      for (Message message: messages) {
        
        // Code coming from http://www.oracle.com/technetwork/java/javamail/faq-135477.html#imapserverbug
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        message.writeTo(bos);
        bos.close();
        SharedByteArrayInputStream bis =
            new SharedByteArrayInputStream(bos.toByteArray());
        ERMessage woMessage = new ERMessage();
        woMessage.setMimeMessage((MimeMessage)message);
        MimeMessage cmsg = new MimeMessage(ERJavaMail.sharedInstance().sessionForMessage(woMessage), bis);
        bis.close();
        
        String[] contentTypeParts = cmsg.getContentType().split(";");
        boolean isDeliveryStatus = false;
        for (int i = 0; i < contentTypeParts.length; i++) {
          if (contentTypeParts[i].contains("multipart/report")) {
            isDeliveryStatus = true;
          }
        }
        if (isDeliveryStatus) {
          if (cmsg.getContent() instanceof MultipartReport) {
            MultipartReport report = (MultipartReport)cmsg.getContent();
            DeliveryStatus fullReport = (DeliveryStatus)report.getReport();
            String action = fullReport.getRecipientDSN(0).getHeader("Action")[0];
            if ("failed".equals(action)) {
              // Original-Recipient: rfc822;<fsdfdsfsdfsdfdsfsdf@me.com>
              String[] originalRecipient = fullReport.getRecipientDSN(0).getHeader("Original-Recipient")[0].split(";");
              if (originalRecipient.length > 1) {
                String emailToRemove = originalRecipient[1];
              } else {
                String emailToRemove = originalRecipient[0];
              }
            }
          }
        }
      }
      imapConnection.closeConnection();
    }
    catch (FolderNotFoundException e) {
      e.printStackTrace();
    }
    catch (MessagingException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public WOActionResults validateEmails() {
    ERXEmailValidator validator = new ERXEmailValidator(true, true);
    if (validator.isValidEmailAddress(_emailFrom, 100, false)) {
      _message = "Valid";
    } else {
      _message = "Invalid";
    }
    return null;
  }
    
}