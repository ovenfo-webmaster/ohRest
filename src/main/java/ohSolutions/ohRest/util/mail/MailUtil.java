package ohSolutions.ohRest.util.mail;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoUtil;
import ohSolutions.ohJpo.dao.Procedure;
import ohSolutions.ohRest.RestUtil;

public class MailUtil {

	final static Logger logger = LogManager.getLogger(MailUtil.class);
	
	private final static String jpoMailHost = "jpo.mail.host";
	private final static String jpoMailPort = "jpo.mail.port";
	private final static String jpoMailUserName = "jpo.mail.username";
	private final static String jpoMailPassword = "jpo.mail.password";
	private final static String jpoMailAttImg = "jpo.mail.attachment.img";
	private final static String jpoMailAttName = "jpo.mail.attachment.name";
	private final static String jpoMailAttId = "jpo.mail.attachment.id";
	private final static String jpoMailSendgridApiKey = "jpo.mail.sendgrid.api.key";
	
    public interface MailUtilListener {
        public void onSendEmail(String id, String msg_id);
    }
    
    private static void onSendEmail(String datasource, String id, String msg_id) throws Exception {
    	onSendEmail(datasource, id, msg_id, null);
    }
    
	@SuppressWarnings("unchecked")
	private static void onSendEmail(String datasource, String id, String msg_id, String properties_folder) throws Exception {
		Jpo ppo;
		try {
			
			ppo = new Jpo(datasource, null, properties_folder);
			ppo.setData("COP", "email_enviado_id", id);
			ppo.setData("COP", "msg_id", msg_id);
			
			Procedure pResult = ppo.procedure("ges.email_editar","COP");
			pResult.input("email_enviado_id", Jpo.INTEGER);
			pResult.input("msg_id", Jpo.STRING);
			pResult.output("estado", Jpo.INTEGER);
			pResult.output("mensaje", Jpo.STRING);
			
			List<Object> resultado = (List<Object>)  pResult.executeL();
			ppo.commit();
			ppo.finalizar();	
			System.out.println("Se grabó correctamente "+resultado.get(0));
			if(!resultado.get(0).equals(1)) {
				logger.debug(resultado.get(1));
			}
			
		} catch (Exception e) {
	    	logger.error(e.getMessage(), e);
			throw new Exception("ohSolutions.Rest - Error after send mail to save");
		}
    }

    public static boolean sendMail(final String templateXML, boolean async, String datasource, String project_prefix) throws Exception {
		if (async) {
			new Thread(() -> {
				try {
					Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix);
					if(datasource != null) {
						onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"));
					}
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}).start();
			return true;
		} else {
			Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix);
			if(datasource != null) {
				onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"));
			}
			return true;
		}
	}
    
    public static boolean sendMailWithPropertie(final String templateXML, boolean async, String datasource, String project_prefix, String properties_folder) throws Exception {
		if (async) {
			new Thread(() -> {
				try {
					Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix, properties_folder);
					if(datasource != null) {
						onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), properties_folder);
					}
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}).start();
			return true;
		} else {
			Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix, properties_folder);
			if(datasource != null) {
				onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), properties_folder);
			}
			return true;
		}
	}
    
    public static boolean sendMail(final String templateXML, boolean async, String datasource) throws Exception {
		return sendMail(templateXML, async, datasource, "");
	}
    
	public static boolean sendMail(final String templateXML, boolean async) throws Exception {
		return sendMail(templateXML, async, null, "");
	}
	
	public static boolean sendMail(final String templateXML, MailUtilListener listener) throws Exception {
		return sendMail(templateXML, listener, "");
	}
	
	
		
	public static boolean sendMail(final String templateXML, MailUtilListener listener, String project_prefix) throws Exception {
		final String templateXMLf = templateXML;
		new Thread(() -> {
			try {
				Map<String, String> resultado = MailUtil.sendMail(templateXMLf, project_prefix);
				listener.onSendEmail(resultado.get("id"), resultado.get("msg_id"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		return true;
	}
		/*
	public static Map<String, String> sendMail(SQLXML XML) throws Exception {
		
		InputStream binaryStream = XML.getBinaryStream();
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = parser.parse(binaryStream);
		
		return getDocument(doc);
		
	}
*/
	public static Map<String, String> sendMail(String templateXML, String project_prefix) throws Exception {
		return sendMail(templateXML, project_prefix, null);
	}
	
	public static Map<String, String> sendMail(String templateXML, String project_prefix, String properties_folder) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(templateXML));

		Document doc = db.parse(is);
		
		return getDocument(doc, project_prefix, properties_folder);

	}
	
	private static Map<String, String> getDocument(Document doc, String project_prefix, String properties_folder) throws Exception {
		
		NodeList nodes = doc.getElementsByTagName("mail");

		Element element = (Element) nodes.item(0);

		String id = getNodeValue(element, "id");
		String from = getNodeValue(element, "from");
		String to = getNodeValue(element, "to");
		String copy = getNodeValue(element, "copy");
		String hidenCopy = getNodeValue(element, "hidenCopy");
		String subject = getNodeValue(element, "subject");
		String body = getNodeValue(element, "body");
		
		String msg_id = sendGrid(from, to, copy, hidenCopy, subject, body, project_prefix, properties_folder);
		
		Map<String, String> resultado = new HashMap<String, String>();
		
		resultado.put("id", id);
		resultado.put("msg_id", msg_id);
		
		return resultado;
		
	}

	private static String getNodeValue(Element e, String tag) {
		NodeList element = e.getElementsByTagName(tag);
		Element item = (Element) element.item(0);
		if(item != null) {
			Node firstChild = item.getFirstChild();
			return firstChild.getNodeValue();
		} else {
			return null;
		}
	}
	
	public static String sendGrid(String from, String to, String copy, String hidenCopy, String subject, String body, String project_prefix) throws Exception {
		return sendGrid(from, to, copy, hidenCopy, subject, body, project_prefix, null);
	}
	
	public static String sendGrid(String from, String to, String copy, String hidenCopy, String subject, String body, String project_prefix, String properties_folder) throws Exception {
		
		RestUtil ru = new RestUtil();
	    
		final String apikey = JpoUtil.getPropertieProyect(project_prefix, jpoMailSendgridApiKey, properties_folder);
		final String attachmentimg = JpoUtil.getPropertieProyect(jpoMailAttImg, properties_folder);
		final String attachmentname = JpoUtil.getPropertieProyect(jpoMailAttName, properties_folder);
		final String attachmentid = JpoUtil.getPropertieProyect(jpoMailAttId, properties_folder);
		
		System.out.println(apikey);
		System.out.println(attachmentimg);
		System.out.println(attachmentname);
		System.out.println(attachmentid);
		
		Email _from = new Email(from);

	    String _subject = subject;
	    Content content = new Content("text/html", body);
	    Mail mail = new Mail();

	    mail.setFrom(_from);
	    mail.setSubject(_subject);
	    mail.addContent(content);
	    
	    Personalization personalizations = new Personalization();

	    if(to != null) {
	    	String[] tos = to.split(";");
	    	for(int i = 0; i < tos.length; i++) {
	    		personalizations.addTo(new Email(tos[i]));
	    	}
	    }
	    if(copy != null) {
	    	String[] copys = copy.split(";");
	    	for(int i = 0; i < copys.length; i++) {
	    		personalizations.addCc(new Email(copys[i]));
	    	}
	    }
	    if(hidenCopy != null) {
	    	String[] hides = hidenCopy.split(";");
	    	for(int i = 0; i < hides.length; i++) {
	    		personalizations.addCc(new Email(hides[i]));
	    	}
	    }
	    
	    mail.addPersonalization(personalizations);
	    
	    Attachments attachments = new Attachments();
	    
	    System.out.println(attachmentimg);
	    
	    attachments.setContent(ru.fileToString(attachmentimg));
	    attachments.setType("image/png");
	    attachments.setFilename(attachmentname);
	    attachments.setContentId(attachmentid);
	    
	    mail.addAttachments(attachments);
	    
	    SendGrid sg = new SendGrid(apikey);
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println("sendgrid respuesta");
	      System.out.println(response);
	      System.out.println(response.getStatusCode());
	      String msg_id = null;
	      if(response.getStatusCode()==202) {
	    	  msg_id = response.getHeaders().get("X-Message-Id");
	      } else {
		      logger.debug(response.getStatusCode());
		      logger.debug(response.getBody());
		      logger.debug(response.getHeaders());
	      }
	      
	      return msg_id;
	    } catch (IOException e) {
	    	logger.error(e.getMessage(), e);
			throw new Exception("ohSolutions.Rest - Error sending email by sendGrid");
	    }
	    
	}

	public static boolean sendSMTP(String from, String to, String copy, String hidenCopy, String subject, String body) throws Exception {

		final String host = JpoUtil.getPropertie(jpoMailHost);
		final String port = JpoUtil.getPropertie(jpoMailPort);
		final String username = JpoUtil.getPropertie(jpoMailUserName);
		final String password = JpoUtil.getPropertie(jpoMailPassword);
		final String attachmentimg = JpoUtil.getPropertie(jpoMailAttImg);
		final String attachmentname = JpoUtil.getPropertie(jpoMailAttName);
		final String attachmentid = JpoUtil.getPropertie(jpoMailAttId);

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(copy));
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(hidenCopy));
			message.setSubject(subject);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Part two is attachment
			BodyPart messageBodyPart = new MimeBodyPart();

			// has attachment image
			DataSource source = new FileDataSource(attachmentimg);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setHeader("Content-ID", "<" + attachmentid + ">");
			messageBodyPart.setFileName(attachmentname);
			multipart.addBodyPart(messageBodyPart);

			// has body
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body, "text/html; charset=utf-8");

			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport.send(message);

		} catch (MessagingException e) {
	    	logger.error(e.getMessage(), e);
			throw new Exception("ohSolutions.Rest - Error sending email by SMTP");
		}

		return true;

	}

}