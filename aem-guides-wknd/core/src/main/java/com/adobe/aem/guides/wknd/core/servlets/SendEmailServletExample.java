package com.adobe.aem.guides.wknd.core.servlets;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
// import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
// import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(
  service = { Servlet.class },
  property = {
    SLING_SERVLET_PATHS + "=/bin/sendemail"
    
  }
)

// @Component(service = { Servlet.class })
// @SlingServletResourceTypes(resourceTypes = "wknd/components/form", methods = { HttpConstants.METHOD_GET,
//         HttpConstants.METHOD_POST }, extensions = "html,txt")
public class SendEmailServletExample extends SlingAllMethodsServlet {

  private static Logger log = LoggerFactory.getLogger(SendEmailServletExample.class);

  @Reference
  private MessageGatewayService messageGatewayService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    JSONObject jsonResponse = new JSONObject();
    boolean sent = false;
    try {
      String[] recipients = { "kartikeya.agarwal2021@gmail.com" };
      sendEmail("This is an test email",
        "This is the email body", recipients);
      response.setStatus(200);
      sent = true;
    } catch (EmailException e) {
      response.setStatus(500);
    }
    try {
      jsonResponse.put("result", sent ? "done" : "something went wrong");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    response.getWriter().write(jsonResponse.toString());
  }

  private void sendEmail(String subjectLine, String msgBody, String[] recipients) throws EmailException {
    Email email = new HtmlEmail();
    for (String recipient : recipients) {
      email.addTo(recipient, recipient);
    }
    email.setSubject(subjectLine);
    email.setMsg(msgBody);
    MessageGateway<Email> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
    if (messageGateway != null) {
      log.debug("sending out email");
      messageGateway.send((Email) email);
    } else {
      log.error("The message gateway could not be retrieved.");
    }
  }
}