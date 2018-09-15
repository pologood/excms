package cn.lonsun.content.onlineDeclaration.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */
@Entity
@Table(name="cms_declaration_record")
public class DeclarationRecordEO extends AMockEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="declaration_id")
    private Long declarationId;

    //转办人
    @Transient
    private String transUserName;


    @Column(name="trans_to_id")
    private Long transToId;

    @Transient
    private String transToName;

    @Column(name="remark")
    private String remark;

    public Long getDeclarationId() {
        return declarationId;
    }

    public void setDeclarationId(Long declarationId) {
        this.declarationId = declarationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransUserName() {
        return transUserName;
    }

    public void setTransUserName(String transUserName) {
        this.transUserName = transUserName;
    }

    public String getTransToName() {
        return transToName;
    }

    public void setTransToName(String transToName) {
        this.transToName = transToName;
    }

    public Long getTransToId() {
        return transToId;
    }

    public void setTransToId(Long transToId) {
        this.transToId = transToId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
