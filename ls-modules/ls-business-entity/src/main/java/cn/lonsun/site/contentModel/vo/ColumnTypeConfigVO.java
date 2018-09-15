package cn.lonsun.site.contentModel.vo;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-23<br/>
 */

public class ColumnTypeConfigVO {
    //链接
    private Integer isLogo = 0;//是否启用Logo
    private Integer height;//图高度
    private Integer width;//图宽度
    private Long num = 0L;//条数
    //留言
    private Integer recType;//接收方式：0：单位接收，1：人接收
    private String recUserNames;//接收名字
    private String recUserIds;//接收人Ids
    private String recUnitNames;//接收单位名称
    private String recUnitIds;//接收单位Ids
    private Integer isLoginGuest = 0;//开启会员登录验证
    private String classCodes;
    private String classNames;

    //是否转办单位
    private Integer turn = 0;

    //转办单位Id
    private String turnUnitIds;
    //转办单位名字
    private String turnUnitNames;


    //文章、图片、视频
    private Integer picWidth;//缩略图宽度
    private Integer picHeight;//缩略图高度
    private Integer contentWidth;//正文宽度
    private Integer isEnableBeauty = 0;//是否启用美图
    private Integer isSensitiveWord = 0;//是否开启敏感词汇
    private Integer isEasyWord = 0;//是否开启易错词
    private Integer isHotWord = 0;//是否开启热词
    private Integer isComment = 0;//是否允许评论
    private Integer isAssess = 0;//是否允许评价

    //(日期池)
    private String datePool;

    //(超时回复天数)
    private Integer limitDay;

    //(黄牌天数)
    private Integer yellowCardDay;

    //(红牌天数)
    private Integer redCardDay;

    private Integer isRedYellow = 0;

    private Integer assessDay;//评价时间

    private Integer isConsole = 0;//是否允许网民咨询

    private Integer isComplaint = 0;//是否允许网民投诉

    private Integer isDeclaration = 0;//是否允许网民申报

    private Integer isVisit = 0;//最少去现场次数

    private String consoleLink;//咨询链接

    private String complaintLink;//投诉链接

    private String declarationLink;//申诉链接

    private Integer visitCount;//现场次数

    private String orderTypeCode;//排序方式

    private String statusCode; //办理状态

    private String statusName;//办理状态名称

    private Integer isLocalUnit = 0;//是否流转区域

    private Integer isWater = 0;//是否启用水印

    public Integer getIsLogo() {
        return isLogo;
    }

    public void setIsLogo(Integer isLogo) {
        this.isLogo = isLogo;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public String getRecUserNames() {
        return recUserNames;
    }

    public void setRecUserNames(String recUserNames) {
        this.recUserNames = recUserNames;
    }

    public String getRecUserIds() {
        return recUserIds;
    }

    public void setRecUserIds(String recUserIds) {
        this.recUserIds = recUserIds;
    }

    public String getRecUnitNames() {
        return recUnitNames;
    }

    public void setRecUnitNames(String recUnitNames) {
        this.recUnitNames = recUnitNames;
    }

    public String getRecUnitIds() {
        return recUnitIds;
    }

    public void setRecUnitIds(String recUnitIds) {
        this.recUnitIds = recUnitIds;
    }

    public Integer getIsEnableBeauty() {
        return isEnableBeauty;
    }

    public void setIsEnableBeauty(Integer isEnableBeauty) {
        this.isEnableBeauty = isEnableBeauty;
    }

    public Integer getIsSensitiveWord() {
        return isSensitiveWord;
    }

    public void setIsSensitiveWord(Integer isSensitiveWord) {
        this.isSensitiveWord = isSensitiveWord;
    }

    public Integer getIsEasyWord() {
        return isEasyWord;
    }

    public void setIsEasyWord(Integer isEasyWord) {
        this.isEasyWord = isEasyWord;
    }

    public Integer getIsHotWord() {
        return isHotWord;
    }

    public void setIsHotWord(Integer isHotWord) {
        this.isHotWord = isHotWord;
    }

    public Integer getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(Integer picWidth) {
        this.picWidth = picWidth;
    }

    public Integer getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(Integer picHeight) {
        this.picHeight = picHeight;
    }

    public Integer getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(Integer contentWidth) {
        this.contentWidth = contentWidth;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public Integer getIsLoginGuest() {
        return isLoginGuest;
    }

    public void setIsLoginGuest(Integer isLoginGuest) {
        this.isLoginGuest = isLoginGuest;
    }

    public String getClassCodes() {
        return classCodes;
    }

    public void setClassCodes(String classCodes) {
        this.classCodes = classCodes;
    }

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public String getDatePool() {
        return datePool;
    }

    public void setDatePool(String datePool) {
        this.datePool = datePool;
    }

    public Integer getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(Integer limitDay) {
        this.limitDay = limitDay;
    }

    public Integer getYellowCardDay() {
        return yellowCardDay;
    }

    public void setYellowCardDay(Integer yellowCardDay) {
        this.yellowCardDay = yellowCardDay;
    }

    public Integer getRedCardDay() {
        return redCardDay;
    }

    public void setRedCardDay(Integer redCardDay) {
        this.redCardDay = redCardDay;
    }

    public Integer getIsRedYellow() {
        return isRedYellow;
    }

    public void setIsRedYellow(Integer isRedYellow) {
        this.isRedYellow = isRedYellow;
    }

    public Integer getIsAssess() {
        return isAssess;
    }

    public void setIsAssess(Integer isAssess) {
        this.isAssess = isAssess;
    }

    public Integer getAssessDay() {
        return assessDay;
    }

    public void setAssessDay(Integer assessDay) {
        this.assessDay = assessDay;
    }

    public Integer getIsConsole() {
        return isConsole;
    }

    public void setIsConsole(Integer isConsole) {
        this.isConsole = isConsole;
    }

    public Integer getIsComplaint() {
        return isComplaint;
    }

    public void setIsComplaint(Integer isComplaint) {
        this.isComplaint = isComplaint;
    }

    public Integer getIsDeclaration() {
        return isDeclaration;
    }

    public void setIsDeclaration(Integer isDeclaration) {
        this.isDeclaration = isDeclaration;
    }

    public String getConsoleLink() {
        return consoleLink;
    }

    public void setConsoleLink(String consoleLink) {
        this.consoleLink = consoleLink;
    }

    public String getComplaintLink() {
        return complaintLink;
    }

    public void setComplaintLink(String complaintLink) {
        this.complaintLink = complaintLink;
    }

    public String getDeclarationLink() {
        return declarationLink;
    }

    public void setDeclarationLink(String declarationLink) {
        this.declarationLink = declarationLink;
    }

    public Integer getIsVisit() {
        return isVisit;
    }

    public void setIsVisit(Integer isVisit) {
        this.isVisit = isVisit;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public String getOrderTypeCode() {
        return orderTypeCode;
    }

    public void setOrderTypeCode(String orderTypeCode) {
        this.orderTypeCode = orderTypeCode;
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

    public Integer getIsLocalUnit() {
        return isLocalUnit;
    }

    public void setIsLocalUnit(Integer isLocalUnit) {
        this.isLocalUnit = isLocalUnit;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public String getTurnUnitIds() {
        return turnUnitIds;
    }

    public void setTurnUnitIds(String turnUnitIds) {
        this.turnUnitIds = turnUnitIds;
    }

    public String getTurnUnitNames() {
        return turnUnitNames;
    }

    public void setTurnUnitNames(String turnUnitNames) {
        this.turnUnitNames = turnUnitNames;
    }

    public Integer getIsWater() {
        return isWater;
    }

    public void setIsWater(Integer isWater) {
        this.isWater = isWater;
    }
}
