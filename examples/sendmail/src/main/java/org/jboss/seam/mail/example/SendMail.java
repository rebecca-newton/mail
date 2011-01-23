/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.seam.mail.example;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.mail.Session;

import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.core.enumurations.ContentDisposition;
import org.jboss.seam.mail.core.enumurations.MessagePriority;
import org.jboss.seam.mail.templating.VelocityMailMessage;
/**
 * 
 * @author Cody Lerum
 *
 */
@Model
public class SendMail
{
   private String text = "This is the alternative text body for mail readers that don't support html";

   @Inject
   private Instance<MailMessage> mailMessage;
   
   @Inject
   private Instance<VelocityMailMessage> velocityMailMessage;
   
   @Inject
   private Session session;
   
   @Inject
   private Person person;   

   public void sendText()
   {
      mailMessage.get()
            .from("Seam Framework", "seam@jboss.org")
            .to(person.getName(), person.getEmail())
            .subject("Text Message from Seam Mail - " + java.util.UUID.randomUUID().toString())
            .textBody(text)
            .send(session);
   }

   public void sendHTML() throws MalformedURLException
   {
      VelocityMailMessage vmm = velocityMailMessage.get();
      
      vmm.from("Seam Framework", "seam@jboss.org")
            .to(person.getName(), person.getEmail())
            .subject("HTML Message from Seam Mail - " + java.util.UUID.randomUUID().toString())
            .templateHTMLFromClassPath("template.html.vm")
            .put("version", "Seam 3")
            .importance(MessagePriority.HIGH)
            .addAttachment(new URL("http://www.seamframework.org/themes/sfwkorg/img/seam_icon_large.png"), "seamLogo.png", ContentDisposition.INLINE);
            vmm.send(session);
   }

   public void sendHTMLwithAlternative() throws MalformedURLException
   {
      velocityMailMessage.get()
            .from("Seam Framework", "seam@jboss.org")
            .to(person.getName(), person.getEmail())
            .subject("HTML+Text Message from Seam Mail - " + java.util.UUID.randomUUID().toString())
            .put("version", "Seam 3")
            .templateHTMLTextAltFromClassPath("template.html.vm", "template.text.vm")
            .importance(MessagePriority.LOW)
            .deliveryReceipt("seam@jboss.org")
            .readReceipt("seam@jboss.org")
            .addAttachment("template.html.vm", "text/html", ContentDisposition.ATTACHMENT)
            .addAttachment(new URL("http://www.seamframework.org/themes/sfwkorg/img/seam_icon_large.png"), "seamLogo.png", ContentDisposition.INLINE)
            .send(session);
   }
}
