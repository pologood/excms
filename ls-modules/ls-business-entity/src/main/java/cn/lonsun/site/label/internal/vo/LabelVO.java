package cn.lonsun.site.label.internal.vo;

/**
 * @author DooCal
 * @ClassName: LabelVO
 * @Description: 系统内置标签
 * @date 2015/8/28 14:58
 */
public class LabelVO {

  private long id;

  private long pId;

  private String name;

  private String config;

  private String description;

  private Boolean isRoot = false;

  private Long isParent = 0L;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getpId() {
    return pId;
  }

  public void setpId(long pId) {
    this.pId = pId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getIsRoot() {
    return isRoot;
  }

  public void setIsRoot(Boolean root) {
    isRoot = root;
  }

  public Long getIsParent() {
    return isParent;
  }

  public void setIsParent(Long isParent) {
    this.isParent = isParent;
  }
}
