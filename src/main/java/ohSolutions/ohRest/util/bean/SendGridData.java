package ohSolutions.ohRest.util.bean;

import java.util.List;

import com.sendgrid.helpers.mail.objects.Attachments;

public class SendGridData {
	
	private String id;
	private String from;
	private String to;
	private String copy;
	private String hidenCopy;
	private String subject;
	private String body;
	private String projectPrefix;
	private String propertiesFolder;
	private String attachmentImg;
	private String attachmentUrl;
	private String attachmentName;
	private String attachmentId;
	private List<Attachments> attachments;
	private String sendgridApiKey;

	private String mail_smtp_host;
	private String mail_smtp_port;
	private String mail_smtp_username;
	private String mail_smtp_password;
	
	private String enable_ssl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCopy() {
		return copy;
	}
	public void setCopy(String copy) {
		this.copy = copy;
	}
	public String getHidenCopy() {
		return hidenCopy;
	}
	public void setHidenCopy(String hidenCopy) {
		this.hidenCopy = hidenCopy;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getProjectPrefix() {
		return projectPrefix;
	}
	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}
	public String getPropertiesFolder() {
		return propertiesFolder;
	}
	public void setPropertiesFolder(String propertiesFolder) {
		this.propertiesFolder = propertiesFolder;
	}
	public String getAttachmentImg() {
		return attachmentImg;
	}
	public void setAttachmentImg(String attachmentImg) {
		this.attachmentImg = attachmentImg;
	}
	public String getAttachmentUrl() {
		return attachmentUrl;
	}
	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public List<Attachments> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachments> attachments) {
		this.attachments = attachments;
	}
	public String getSendgridApiKey() {
		return sendgridApiKey;
	}
	public void setSendgridApiKey(String sendgridApiKey) {
		this.sendgridApiKey = sendgridApiKey;
	}
	public String getMail_smtp_host() {
		return mail_smtp_host;
	}
	public void setMail_smtp_host(String mail_smtp_host) {
		this.mail_smtp_host = mail_smtp_host;
	}
	public String getMail_smtp_port() {
		return mail_smtp_port;
	}
	public void setMail_smtp_port(String mail_smtp_port) {
		this.mail_smtp_port = mail_smtp_port;
	}
	public String getMail_smtp_username() {
		return mail_smtp_username;
	}
	public void setMail_smtp_username(String mail_smtp_username) {
		this.mail_smtp_username = mail_smtp_username;
	}
	public String getMail_smtp_password() {
		return mail_smtp_password;
	}
	public void setMail_smtp_password(String mail_smtp_password) {
		this.mail_smtp_password = mail_smtp_password;
	}
	public String getEnable_ssl() {
		return enable_ssl;
	}
	public void setEnable_ssl(String enable_ssl) {
		this.enable_ssl = enable_ssl;
	}
	
}