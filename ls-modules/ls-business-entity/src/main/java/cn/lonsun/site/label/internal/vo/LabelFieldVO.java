package cn.lonsun.site.label.internal.vo;

/**
 * @author DooCal
 * @ClassName: LableFieldVO
 * @Description:
 * @date 2015/9/15 15:59
 */
public class LabelFieldVO {

  private String fieldname;

  private String datatype;

  private String allowval;

  private String defaultval;

  private String description;

  public String getFieldname() {
    return fieldname;
  }

  public void setFieldname(String fieldname) {
    this.fieldname = fieldname;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datetype) {
    this.datatype = datetype;
  }

  public String getAllowval() {
    return allowval;
  }

  public void setAllowval(String allowval) {
    this.allowval = allowval;
  }

  public String getDefaultval() {
    return defaultval;
  }

  public void setDefaultval(String defaultval) {
    this.defaultval = defaultval;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
