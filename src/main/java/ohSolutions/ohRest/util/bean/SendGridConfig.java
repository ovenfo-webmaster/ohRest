package ohSolutions.ohRest.util.bean;

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
	
}