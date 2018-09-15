package cn.lonsun.delegatemgr.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 建议议案实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-28<br/>
 */
@Entity
@Table(name = "cms_proposal")
public class ProposalEO extends AMockEntity {
    private static final long serialVersionUID = -1300742296285581640L;

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    //届
    @Column(name="session_")
    private String session;

    //编号
    @Column(name="num")
    private Long num;

    //分类
    @Column(name="type")
    private String type;

    //标题
    @Column(name="title")
    private String title;

    //领衔人
    @Column(name="leader")
    private String leader;

    //内容
    @Column(name="content")
    private String content;

    //工作单位
    @Column(name="work_unit")
    private String workUnit;

    //职务
    @Column(name="work")
    private String work;

    //通信地址
    @Column(name="address")
    private String address;

    //邮编
    @Column(name="zip_code")
    private String zipCode;

    //联系方式
    @Column(name="phone")
    private String phone;

    //附议人
    @Column(name="attendant")
    private String attendant;

    //主办单位
    @Column(name="organizer")
    private String organizer;

    //协办单位
    @Column(name="co_organizer")
    private String coOrganizer;

    //领衔办理
    @Column(name="lead_deal")
    private String headDeal;

    //处理状态
    @Column(name="deal_status")
    private String dealStatus;

    //交办时间
    @Column(name="delivery_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date deliveryTime;

    //办复时间
    @Column(name="reply_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date replyTime;

    //办理结果
    @Column(name="result")
    private String result;

    //滚动办理
    @Column(name="roll_deal")
    private String rollDeal;

    @Column(name="site_id")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getCoOrganizer() {
        return coOrganizer;
    }

    public void setCoOrganizer(String coOrganizer) {
        this.coOrganizer = coOrganizer;
    }

    public String getHeadDeal() {
        return headDeal;
    }

    public void setHeadDeal(String headDeal) {
        this.headDeal = headDeal;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRollDeal() {
        return rollDeal;
    }

    public void setRollDeal(String rollDeal) {
        this.rollDeal = rollDeal;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
