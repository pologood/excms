package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import javax.persistence.*;
/**
 * 操作日志
 * Created by zhangchao on 2016/12/21.
 */

@Entity
@Table(name="CMS_BBS_LOG")
public class BbsLogEO extends ABaseEntity {

    public enum Status {
        Yes(1), // 支持
        No(0);// 反对
        private Integer status;
        private Status(Integer status){
            this.status=status;
        }
        public Integer getStatus(){
            return status;
        }
    }

    public enum Type {
        Tourist, // 游客
        Member,// 会员
        User;// 用户
    }

    public enum Operation {
        View, //浏览
        Audit;//审核

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="LOG_ID")
    private Long logId;

    @Column(name="CASE_ID")
    private Long caseId;

    @Column(name="TYPE_")
    private String type = Type.Tourist.toString();

    @Column(name="OPERATION")
    private String operation = Operation.View.toString();

    @Column(name="MEMBER_ID")
    private Long memberId;

    @Column(name="MEMBER_NAME")
    private String memberName;

    //支持 、反对操作
    @Column(name="STATUS")
    private Integer status;


    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
