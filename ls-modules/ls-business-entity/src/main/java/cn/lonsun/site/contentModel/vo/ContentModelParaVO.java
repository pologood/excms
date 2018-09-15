package cn.lonsun.site.contentModel.vo;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */

public class ContentModelParaVO {
    private Integer recType;
    private String recUserName;
    private String recUserId;
    private String recUnitName;
    private Long recUnitId;
    private String classCode;
    private String className;
    private String statusCode;
    private String statusName;

    //是否转办单位
    private Integer turn=0;

    //转办单位Id
    private String turnUnitId;
    //转办单位名字
    private String turnUnitName;


    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public String getTurnUnitId() {
        return turnUnitId;
    }

    public void setTurnUnitId(String turnUnitId) {
        this.turnUnitId = turnUnitId;
    }

    public String getTurnUnitName() {
        return turnUnitName;
    }

    public void setTurnUnitName(String turnUnitName) {
        this.turnUnitName = turnUnitName;
    }

    private String link;

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public String getRecUserName() {
        return recUserName;
    }

    public void setRecUserName(String recUserName) {
        this.recUserName = recUserName;
    }

    public String getRecUnitName() {
        return recUnitName;
    }

    public void setRecUnitName(String recUnitName) {
        this.recUnitName = recUnitName;
    }

    public Long getRecUnitId() {
        return recUnitId;
    }

    public void setRecUnitId(Long recUnitId) {
        this.recUnitId = recUnitId;
    }

    public String getRecUserId() {
        return recUserId;
    }

    public void setRecUserId(String recUserId) {
        this.recUserId = recUserId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
