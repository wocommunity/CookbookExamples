package org.wocommunity.cookbook.examples.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponent;
import er.javamail.ERMailDeliveryHTML;
import er.javamail.ERMailDeliveryPlainText;

public class CBEmailExamples extends ERXComponent {

  private String _emailFrom;
  private String _emailTo;

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
    } catch (Exception e) {
      // do something ...
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
    } catch (Exception e) {
      // do something ...
    }
    return null;
  }

}