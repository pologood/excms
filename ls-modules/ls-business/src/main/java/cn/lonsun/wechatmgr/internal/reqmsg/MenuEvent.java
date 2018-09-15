package cn.lonsun.wechatmgr.internal.reqmsg;

public class MenuEvent extends BaseMessage {

	private String Event;
	
	private String EventKey;

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}
	
	
}
