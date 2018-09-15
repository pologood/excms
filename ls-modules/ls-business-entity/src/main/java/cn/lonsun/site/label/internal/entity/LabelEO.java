package cn.lonsun.site.label.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author DooCal
 * @ClassName: LebelEO
 * @Description: 系统内置标签实体类
 * @date 2015/8/28 14:58
 */
@Entity
@Table(name = "CMS_LABEL")
public class LabelEO extends AMockEntity implements Serializable {

  private static final long serialVersionUID = 7450842681073468017L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  private Long id;

  @Column(name = "PARENT_ID")
  private Long parentId;

  /*标签名称*/
  @Column(name = "LABEL_NAME")
  private String labelName;

  /*标签配置*/
  @Column(name = "LABEL_CONFIG")
  private String labelConfig;

  /*标签描述*/
  @Column(name = "LABEL_NOTES")
  private String labelNotes;

  @Column(name = "LABEL_TYPE")
  private Integer labelType;

  @Column(name = "IS_PARENT")
  private Long isParent = 0L;


  public String getLabelNotes() {
    return labelNotes;
  }

  public void setLabelNotes(String labelNotes) {
    this.labelNotes = labelNotes;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getLabelConfig() {
    return labelConfig;
  }

  public void setLabelConfig(String labelConfig) {
    this.labelConfig = labelConfig;
  }

  public String getLabelName() {
    return labelName;
  }

  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }

  public Integer getLabelType() {
    return labelType;
  }

  public void setLabelType(Integer labelType) {
    this.labelType = labelType;
  }

  public Long getIsParent() {
    return isParent;
  }

  public void setIsParent(Long isParent) {
    this.isParent = isParent;
  }
}
