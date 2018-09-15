package cn.lonsun.content.vo;

import cn.lonsun.common.vo.PageQueryVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: ContentPageVO
 * @Description: 新闻分页
 * @date 2015年10月15日 上午8:19:46
 */
public class ContentPageVO extends PageQueryVO {

    //站点
    private Long siteId;
    //栏目
    private Long columnId;
    //新闻标题
    private String title;

    private String columnIds;
    //创建人
    private Long createUserId;
    //类型
    private Integer type;
    //条件
    private String condition;
    //状态
    private Integer status;
    //多条件筛选map
    private Map<String,Integer> conditionMap;
    //栏目类型
    private String typeCode;
    //栏目类型集
    private String[] typeCodes;

    //作者
    private String author;
    //来源
    private String resources;

    private Integer orderType = 1;

    private Integer isReply = 1;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date startTime;

    //结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date endTime;

    private String ids;//在文件解读页面查询已关联的文章需要根据Id检索
    private Long[] idArray;

    private boolean isMember;
    private String member;

    private Integer isPublish;
    private Integer num;//需要返回的数据量

    private Boolean noAuthority;//是否判断权限，从前台查询时传入true

    public ContentPageVO() {
    }

    public ContentPageVO(Long siteId, Long columnId, Integer status, Long[] idArray, String typeCode) {
        this.siteId = siteId;
        this.columnId = columnId;
        this.status = status;
        this.idArray = idArray;
        this.typeCode = typeCode;
    }

    public Long[] getIdArray() {
        return idArray;
    }

    public void setIdArray(Long[] idArray) {
        this.idArray = idArray;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public boolean getIsMember() {
        return isMember;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String[] getTypeCodes() {
        return typeCodes;
    }

    public void setTypeCodes(String[] typeCodes) {
        this.typeCodes = typeCodes;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getIsReply() {
        return isReply;
    }

    public void setIsReply(Integer isReply) {
        this.isReply = isReply;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getNoAuthority() {
        return noAuthority;
    }

    public void setNoAuthority(Boolean noAuthority) {
        this.noAuthority = noAuthority;
    }

    public Map<String, Integer> getConditionMap() {
        return conditionMap;
    }

    public void setConditionMap(Map<String, Integer> conditionMap) {
        this.conditionMap = conditionMap;
    }
}
