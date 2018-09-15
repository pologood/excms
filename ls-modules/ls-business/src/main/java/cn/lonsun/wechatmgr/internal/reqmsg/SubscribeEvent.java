package cn.lonsun.wechatmgr.internal.reqmsg;

public class SubscribeEvent extends BaseMessage {
	private String Event;

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}
	
}
