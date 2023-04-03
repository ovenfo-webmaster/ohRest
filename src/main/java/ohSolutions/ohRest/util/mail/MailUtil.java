package ohSolutions.ohRest.util.mail;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoUtil;
import ohSolutions.ohJpo.dao.Procedure;
import ohSolutions.ohRest.RestUtil;
import ohSolutions.ohRest.util.bean.SendGridConfig;
import ohSolutions.ohRest.util.bean.SendGridData;
import ohSolutions.ohRest.util.bean.SendGridResponse;

public class MailUtil {

	final static Logger logger = LogManager.getLogger(MailUtil.class);
	/*
	private final static String jpoMailHost = "jpo.mail.host";
	private final static String jpoMailPort = "jpo.mail.port";
	private final static String jpoMailUserName = "jpo.mail.username";
	private final static String jpoMailPassword = "jpo.mail.password";*/
	private final static String jpoMailAttImg = "jpo.mail.attachment.img";
	private final static String jpoMailAttName = "jpo.mail.attachment.name";
	private final static String jpoMailAttId = "jpo.mail.attachment.id";
	private final static String jpoMailSendgridApiKey = "jpo.mail.sendgrid.api.key";
	
    public interface MailUtilListener {
        public void onSendEmail(String id, String msg_id);
    }
    
    private static void onSendEmail(String datasource, String id, String msg_id, Jpo customJpo) throws Exception {
    	onSendEmail(datasource, id, msg_id, null, customJpo);
    }
    
	@SuppressWarnings("unchecked")
	private static void onSendEmail(String datasource, String id, String msg_id, String properties_folder, Jpo customJpo) throws Exception {
		
		Jpo ppo;
		
		try {
			
			if(customJpo != null) {
				ppo = customJpo;
			} else {
				ppo = new Jpo(datasource, null, properties_folder);
			}
			
			System.out.println("conectando "+ppo.getSourceInfo()+" - "+id+" - "+msg_id);
			
			Procedure pResult = ppo.procedure("ges.email_editar");
			pResult.input("email_enviado_id", id, Jpo.INTEGER);
			pResult.input("msg_id", msg_id, Jpo.STRING);
			pResult.output("estado", Jpo.INTEGER);
			pResult.output("mensaje", Jpo.STRING);
			
			List<Object> resultado = (List<Object>)  pResult.executeL();
			
			if(customJpo == null) {
				ppo.commit();
				ppo.finalizar();
			}
			
			logger.debug("Se grabó correctamente "+resultado.get(0));
			System.out.println(resultado);
			
			if(!resultado.get(0).equals(1)) {
				logger.debug(resultado.get(1));
			}
			
		} catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    	e.printStackTrace();
			throw new Exception("ohSolutions.Rest - Error after send mail to save");
		}
    }

    public static boolean sendMail(final String templateXML, boolean async, String datasource, String project_prefix) throws Exception {
		if (async) {
			new Thread(() -> {
				try {
					Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix);
					if(datasource != null) {
						onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), null);
					}
				} catch (Exception e) {
					logger.debug(e.getMessage());
					e.printStackTrace();
				}
			}).start();
			return true;
		} else {
			Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix);
			if(datasource != null) {
				onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), null);
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

	public static boolean sendMail(final String templateJSON, SendGridConfig config) throws Exception {
		if (config.isAsync()) {
			new Thread(() -> {
				try {
					SendGridResponse resultado = getDocument(templateJSON, config);
					if(config.getDataSource() != null){
						onSendEmail(config.getDataSource(), resultado.getId(), resultado.getMsg_id(), config.getJpo());
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					logger.debug(e.getMessage());
					e.printStackTrace();
				}
			}).start();
			return true;
		} else {
			SendGridResponse resultado = getDocument(templateJSON, config);
			if(config.getDataSource() != null){
				onSendEmail(config.getDataSource(), resultado.getId(), resultado.getMsg_id(), config.getJpo());
			}
			return true;
		}
		
	}
	
	private static SendGridResponse getDocument(String doc, SendGridConfig config) throws Exception {

		JSONObject mail = new JSONObject(doc);
		
		SendGridData sendData = new SendGridData();
		
		String id = ""+mail.getInt("id");
		
		sendData.setFrom(mail.has("from") ? mail.getString("from") : null);
		sendData.setTo(mail.has("to") ? mail.getString("to") : null);
		sendData.setCopy(mail.has("copy") ? mail.getString("copy") : null);
		sendData.setHidenCopy(mail.has("hidenCopy") ? mail.getString("hidenCopy") : null);
		sendData.setSubject(mail.has("subject") ? mail.getString("subject") : null);
		sendData.setBody(mail.has("body") ? mail.getString("body") : null);
		
		sendData.setProjectPrefix(config.getProjectPrefix());
		sendData.setPropertiesFolder(config.getPropertiesFolder());
		
		sendData.setAttachmentUrl(mail.has("attach_img_url") ? mail.getString("attach_img_url") : null);
		sendData.setAttachmentImg(config.getAttachmentImg());
		sendData.setAttachmentName(mail.has("attach_img_name") ? mail.getString("attach_img_name") : config.getAttachmentName());
		sendData.setAttachmentId(mail.has("attach_img_id") ? mail.getString("attach_img_id") : config.getAttachmentId());
		
		sendData.setAttachments(config.getAttachments());
		sendData.setSendgridApiKey(mail.has("sendgrid_api_key") ? mail.getString("sendgrid_api_key") : null);
		

		sendData.setMail_smtp_host(mail.has("mail_smtp_host") ? mail.getString("mail_smtp_host") : null);
		sendData.setMail_smtp_port(mail.has("mail_smtp_port") ? mail.getString("mail_smtp_port") : null);
		sendData.setMail_smtp_username(mail.has("mail_smtp_username") ? mail.getString("mail_smtp_username") : null);
		sendData.setMail_smtp_password(mail.has("mail_smtp_password") ? mail.getString("mail_smtp_password") : null);
		sendData.setEnable_ssl(mail.has("enable_ssl") ? mail.getString("enable_ssl") : null);

		System.out.println("testing 2.5 ------------");
		
		
		String msg_id = null;
		
		if(sendData.getMail_smtp_host() != null) {

			msg_id = sendSMTP(sendData);
			
			
		} else {

			msg_id = sendGrid(sendData);
		}
		
		return new SendGridResponse(id, msg_id);
		
	}
	
    public static boolean sendMailWithPropertie(final String templateXML, boolean async, String datasource, String project_prefix, String properties_folder) throws Exception {
		if (async) {
			new Thread(() -> {
				try {
					Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix, properties_folder);
					if(datasource != null) {
						onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), properties_folder, null);
					}
				} catch (Exception e) {
					logger.debug(e.getMessage());
					e.printStackTrace();
				}
			}).start();
			return true;
		} else {
			Map<String, String> resultado = MailUtil.sendMail(templateXML, project_prefix, properties_folder);
			if(datasource != null) {
				onSendEmail(datasource, resultado.get("id"), resultado.get("msg_id"), properties_folder, null);
			}
			return true;
		}
	}
	
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
		
		SendGridData sendData = new SendGridData();
		
		sendData.setFrom(from);
		sendData.setTo(to);
		sendData.setCopy(copy);
		sendData.setHidenCopy(hidenCopy);
		sendData.setSubject(subject);
		sendData.setBody(body);
		sendData.setProjectPrefix(project_prefix);
		sendData.setPropertiesFolder(properties_folder);
		
		return sendGrid(sendData);
		
	}
	
	public static String sendGrid(SendGridData sendData) throws Exception {
		
		RestUtil ru = new RestUtil();
	    
		final String apikey = sendData.getSendgridApiKey() != null ? sendData.getSendgridApiKey() : JpoUtil.getPropertieProyect(sendData.getProjectPrefix(), jpoMailSendgridApiKey, sendData.getPropertiesFolder());
		
		final String attachmenturl = sendData.getAttachmentUrl();
		final String attachmentimg = sendData.getAttachmentImg() != null ? sendData.getAttachmentImg() : JpoUtil.getPropertieProyect(jpoMailAttImg, sendData.getPropertiesFolder());
		final String attachmentname = sendData.getAttachmentName() != null ? sendData.getAttachmentName() : JpoUtil.getPropertieProyect(jpoMailAttName, sendData.getPropertiesFolder());
		final String attachmentid = sendData.getAttachmentId() != null ? sendData.getAttachmentId() : JpoUtil.getPropertieProyect(jpoMailAttId, sendData.getPropertiesFolder());

		logger.debug(apikey);
		
		Email _from = new Email(sendData.getFrom());

	    String _subject = sendData.getSubject();
	    Content content = new Content("text/html", sendData.getBody());
	    Mail mail = new Mail();

	    mail.setFrom(_from);
	    mail.setSubject(_subject);
	    mail.addContent(content);
	    
	    Personalization personalizations = new Personalization();

	    if(sendData.getTo() != null) {
	    	String[] tos = sendData.getTo().split(";");
	    	for(int i = 0; i < tos.length; i++) {
	    		personalizations.addTo(new Email(tos[i]));
	    	}
	    }
	    if(sendData.getCopy() != null) {
	    	String[] copys = sendData.getCopy().split(";");
	    	for(int i = 0; i < copys.length; i++) {
	    		personalizations.addCc(new Email(copys[i]));
	    	}
	    }
	    if(sendData.getHidenCopy() != null) {
	    	String[] hides = sendData.getHidenCopy().split(";");
	    	for(int i = 0; i < hides.length; i++) {
	    		personalizations.addCc(new Email(hides[i]));
	    	}
	    }
	    
	    mail.addPersonalization(personalizations);

	    if(attachmentname != null && attachmentid != null && (attachmentimg != null || attachmenturl != null)){
	    	
		    Attachments attachments = new Attachments();
		    
		    if(attachmenturl != null) {
		    	attachments.setContent(ru.urlFileToString(attachmenturl));
		    } else {
			    if(attachmentimg != null) {
			    	attachments.setContent(ru.fileToString(attachmentimg));
			    }
		    }
		    
		    attachments.setType("image/png");
		    attachments.setFilename(attachmentname);
		    attachments.setContentId(attachmentid);
		    
		    mail.addAttachments(attachments);
		    
	    }
	    
	    // Adding new Attachments
	    List<Attachments> files = sendData.getAttachments();
	    if(files != null && files.size() > 0) {
	    	for(int i = 0; i < files.size(); i++) {
	    		mail.addAttachments(files.get(i));
	    	}
	    }
	    // -----------------------
	    System.out.println("Sending mail for "+apikey);
	    SendGrid sg = new SendGrid(apikey);
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      
	      logger.debug(response);
	      System.out.println("Response sendgrid");
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders().toString());

	      String msg_id = null;
	      if(response.getStatusCode()==202) {
	    	  msg_id = response.getHeaders().get("X-Message-Id");
	      } else {
		      logger.debug(response.getStatusCode());
		      logger.debug(response.getBody());
		      logger.debug(response.getHeaders());
	      }
	      System.out.println(msg_id);
	      return msg_id;
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	logger.error(e.getMessage(), e);
			throw new Exception("ohSolutions.Rest - Error sending email by sendGrid");
	    }
	    
	}

	public static String sendSMTP(SendGridData sendData) throws Exception {

		final String host = sendData.getMail_smtp_host();
		final String port = sendData.getMail_smtp_port();
		final String username = sendData.getMail_smtp_username();
		final String password = sendData.getMail_smtp_password();
		/*
		final String attachmentimg = JpoUtil.getPropertie(jpoMailAttImg);
		final String attachmentname = JpoUtil.getPropertie(jpoMailAttName);
		final String attachmentid = JpoUtil.getPropertie(jpoMailAttId);
		
		*/
		String from = sendData.getFrom();
		String to = sendData.getTo();
		String copy = sendData.getCopy();
		String hidenCopy = sendData.getHidenCopy();
		String subject = sendData.getSubject();
		String body = sendData.getBody();
		
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        
        if(sendData.getEnable_ssl().equals("1")) {
            properties.put("mail.smtp.starttls.enable", "true"); //TL
        }
		
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
        });

        try {

            Message message = new MimeMessage(session);
	            
	            message.setFrom(new InternetAddress(from));
	            
				if(to != null && to.length() > 0) {
					String[] _tos = to.split(";");
					for(int i = 0; i < _tos.length; i++) {
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(_tos[i]));
					}
				}
				if(copy != null && copy.length() > 0) {
					String[] _copys = copy.split(";");
					for(int i = 0; i < _copys.length; i++) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(_copys[i]));
					}
				}
				
				if(hidenCopy != null && hidenCopy.length() > 0) {
					String[] _hidenCopys = hidenCopy.split(";");
					for(int i = 0; i < _hidenCopys.length; i++) {
						message.addRecipient(Message.RecipientType.BCC, new InternetAddress(_hidenCopys[i]));
					}
				}
	            
	            message.setSubject(subject);
	            message.setContent(body, "text/html; charset=utf-8");
	            
	            /*
	             * 
			// has attachment image
			DataSource source = new FileDataSource(attachmentimg);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setHeader("Content-ID", "<" + attachmentid + ">");
			messageBodyPart.setFileName(attachmentname);
			multipart.addBodyPart(messageBodyPart);

	             * */
	
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
			throw new Exception("ohSolutions.Rest - Error sending email by SMTP");
        }
        
		return "1";

	}

}