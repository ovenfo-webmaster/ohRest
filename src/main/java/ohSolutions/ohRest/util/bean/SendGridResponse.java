package ohSolutions.ohRest.util.bean;

public class SendGridResponse {
	
	private String id;
	private String msg_id;
	
	public SendGridResponse(String id, String msg_id){
		this.id = id;
		this.msg_id = msg_id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	
}