package ohSolutions.ohRest.util.bean;

import java.util.List;

import com.sendgrid.helpers.mail.objects.Attachments;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohRest.util.mail.MailUtil.MailUtilListener;

public class SendGridConfig {
	
	private boolean async;
	private String dataSource = "";
	private String propertiesFolder;
	private String projectPrefix;
	private MailUtilListener listener;
	private String attachmentImg;
	private String attachmentName;
	private String attachmentId;
	private List<Attachments> attachments;
	private Jpo jpo;
	
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getPropertiesFolder() {
		return propertiesFolder;
	}
	public void setPropertiesFolder(String propertiesFolder) {
		this.propertiesFolder = propertiesFolder;
	}
	public String getProjectPrefix() {
		return projectPrefix;
	}
	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}
	public MailUtilListener getListener() {
		return listener;
	}
	public void setListener(MailUtilListener listener) {
		this.listener = listener;
	}
	public String getAttachmentImg() {
		return attachmentImg;
	}
	public void setAttachmentImg(String attachmentImg) {
		this.attachmentImg = attachmentImg;
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
	public Jpo getJpo() {
		return jpo;
	}
	public void setJpo(Jpo jpo) {
		this.jpo = jpo;
	}
	
}