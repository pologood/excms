package cn.lonsun.content.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


public class GuestBookVO{
	private Long id;//从表id
	private String title;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date publish_date;
	private Long hit=0L;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date create_date;
	private Long user_id;
	private String user_name;
	private String response_content;
	private String guestbook_content;
	private String person_ip;
	private String person_name;
	private Long base_content_id;//主表id
	private Integer is_publish;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date reply_date;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date update_date;//从表的更新时间
	private Integer is_response;
	private Integer guestbook_type;
	private Long person_phone;
	private Long column_id;
	private Integer show_mark;
	private Long receive_id;
	private String receive_name;
	private String name;//栏目名
	private String type_code;//内容类型
	private String resource_type;//来源
	private String open_id;
	private String comment_code;
	private String comment_name;

	private String class_code;
	private String class_name;

	private String receive_user_code;
	private String receive_user_name;
	private Integer rec_type;

	
	private String link;// 生成静态时使用
	
	public Integer getIs_response() {
		return is_response;
	}
	public void setIs_response(Integer is_response) {
		this.is_response = is_response;
	}
	public Integer getGuestbook_type() {
		return guestbook_type;
	}
	public void setGuestbook_type(Integer guestbook_type) {
		this.guestbook_type = guestbook_type;
	}
	public Long getPerson_phone() {
		return person_phone;
	}
	public void setPerson_phone(Long person_phone) {
		this.person_phone = person_phone;
	}
	public Long getBase_content_id() {
		return base_content_id;
	}
	public void setBase_content_id(Long base_content_id) {
		this.base_content_id = base_content_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getPublish_date() {
		return publish_date;
	}
	public void setPublish_date(Date publish_date) {
		this.publish_date = publish_date;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getResponse_content() {
		return response_content;
	}
	public void setResponse_content(String response_content) {
		this.response_content = response_content;
	}
	public String getGuestbook_content() {
		return guestbook_content;
	}
	public void setGuestbook_content(String guestbook_content) {
		this.guestbook_content = guestbook_content;
	}
	public String getPerson_ip() {
		return person_ip;
	}
	public void setPerson_ip(String person_ip) {
		this.person_ip = person_ip;
	}
	public String getPerson_name() {
		return person_name;
	}
	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getIs_publish() {
		return is_publish;
	}
	public void setIs_publish(Integer is_publish) {
		this.is_publish = is_publish;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Date getReply_date() {
		return reply_date;
	}
	public void setReply_date(Date reply_date) {
		this.reply_date = reply_date;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Long getColumn_id() {
		return column_id;
	}
	public void setColumn_id(Long column_id) {
		this.column_id = column_id;
	}
	public Integer getShow_mark() {
		return show_mark;
	}
	public void setShow_mark(Integer show_mark) {
		this.show_mark = show_mark;
	}
	public Long getReceive_id() {
		return receive_id;
	}
	public void setReceive_id(Long receive_id) {
		this.receive_id = receive_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType_code() {
		return type_code;
	}
	public void setType_code(String type_code) {
		this.type_code = type_code;
	}
	public String getReceive_name() {
		return receive_name;
	}

	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
	}

	public Long getHit() {
		return hit;
	}

	public void setHit(Long hit) {
		this.hit = hit;
	}

	public String getResource_type() {
		return resource_type;
	}

	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
	}

	public String getComment_code() {
		return comment_code;
	}

	public void setComment_code(String comment_code) {
		this.comment_code = comment_code;
	}

	public String getOpen_id() {
		return open_id;
	}

	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}

	public String getComment_name() {
		return comment_name;
	}

	public void setComment_name(String comment_name) {
		this.comment_name = comment_name;
	}

	public String getClass_code() {
		return class_code;
	}

	public void setClass_code(String class_code) {
		this.class_code = class_code;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getReceive_user_code() {
		return receive_user_code;
	}

	public void setReceive_user_code(String receive_user_code) {
		this.receive_user_code = receive_user_code;
	}

	public String getReceive_user_name() {
		return receive_user_name;
	}

	public void setReceive_user_name(String receive_user_name) {
		this.receive_user_name = receive_user_name;
	}

	public Integer getRec_type() {
		return rec_type;
	}

	public void setRec_type(Integer rec_type) {
		this.rec_type = rec_type;
	}
}
