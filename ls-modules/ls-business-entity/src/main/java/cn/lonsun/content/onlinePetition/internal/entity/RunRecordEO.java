package cn.lonsun.content.onlinePetition.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Entity
@Table(name="cms_run_record")
public class RunRecordEO extends AMockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="petition_id")
    private Long petitionId;

    @Column(name="trans_user_name")
    private String transUserName;

    @Column(name="trans_to_id")
    private Long transToId;

    @Column(name="trans_to_name")
    private String transToName;

    @Column(name="trans_ip")
    private String transIp;

    @Column(name="remark")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetitionId() {
        return petitionId;
    }

    public void setPetitionId(Long petitionId) {
        this.petitionId = petitionId;
    }

    public String getTransUserName() {
        return transUserName;
    }

    public void setTransUserName(String transUserName) {
        this.transUserName = transUserName;
    }

    public Long getTransToId() {
        return transToId;
    }

    public void setTransToId(Long transToId) {
        this.transToId = transToId;
    }

    public String getTransToName() {
        return transToName;
    }

    public void setTransToName(String transToName) {
        this.transToName = transToName;
    }

    public String getTransIp() {
        return transIp;
    }

    public void setTransIp(String transIp) {
        this.transIp = transIp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
