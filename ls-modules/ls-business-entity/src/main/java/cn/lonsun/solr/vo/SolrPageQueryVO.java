package cn.lonsun.solr.vo;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2016-1-15 8:15
 */
public class SolrPageQueryVO extends PageQueryVO {

    public SolrPageQueryVO(String index) {
        this.keywords = index;
        this.setSortField("createDate");
        this.setPageIndex(0L);
        this.setPageSize(10);
    }

    public SolrPageQueryVO() {
        super();
        this.setSortField("createDate");
        this.setPageIndex(0L);
        this.setPageSize(10);
    }

    public enum TypeCode {
        news,
        articleNews,
        pictureNews,
        videoNews,
        workGuide,
        guestBook,
        knowledgeBase,
        public_content
    }

    public enum FromCode {
        all,
        title,
        remark,
        content,
        fileNum,
        indexNum
    }

    public enum DateCode {
        day,
        week,
        month,
        year
    }

    private Long siteId;

    private Long columnId;//栏目ID

    private String columnIds;

    private String keywords; //查询关键字

    private String typeCode; //查询类型 articleNews 、pictureNews 、videoNews 、workGuide

    private String fromCode;  //查询来源  title 、remark、content

    private String datecode; //时间段

    private Date beginDate; //开始时间

    private Date endDate; //结束时间

    private String excColumns;//排除的栏目

    private Boolean isGroup = false;//是否分组

    private String excIds;//排除的contentId

    private boolean islight = true;//是否加高亮 默认高亮

    private String type; //留言类型

    private String indexNum;// 索引号

    private String fileNum;// 文号

    private List<IndicatorEO> indicatorEOs;

    private Long tableColumnId; //相关资源表格资源栏目

    private boolean fuzzySearch=false;//模糊查找

    /**
     * 需要过滤的关键词， not in 关系
     */
    private String[] filterKeyWords;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnIds() {
        if (StringUtils.isEmpty(columnIds))
            columnIds = null;
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getDatecode() {
        return datecode;
    }

    public void setDatecode(String datecode) {
        this.datecode = datecode;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getExcColumns() {
        return excColumns;
    }

    public void setExcColumns(String excColumns) {
        this.excColumns = excColumns;
    }

    public boolean getIslight() {
        return islight;
    }

    public void setIslight(boolean islight) {
        this.islight = islight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<IndicatorEO> getIndicatorEOs() {
        return indicatorEOs;
    }

    public void setIndicatorEOs(List<IndicatorEO> indicatorEOs) {
        this.indicatorEOs = indicatorEOs;
    }

    public Long getTableColumnId() {
        return tableColumnId;
    }

    public void setTableColumnId(Long tableColumnId) {
        this.tableColumnId = tableColumnId;
    }

    public String getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(String indexNum) {
        this.indexNum = indexNum;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public static Date timeFormat(Date date, int hour, int second, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (null != date) {
            if (calendar.get(calendar.HOUR_OF_DAY) <= 0) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
            }

            if (calendar.get(calendar.SECOND) <= 0) {
                calendar.set(Calendar.SECOND, second);
            }

            if (calendar.get(calendar.MINUTE) <= 0) {
                calendar.set(Calendar.MINUTE, minute);
            }
        }
        return calendar.getTime();
    }

    public String[] getFilterKeyWords() {
        return filterKeyWords;
    }

    public void setFilterKeyWords(String[] filterKeyWords) {
        this.filterKeyWords = filterKeyWords;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public String getExcIds() {
        return excIds;
    }

    public void setExcIds(String excIds) {
        this.excIds = excIds;
    }

    public boolean isFuzzySearch() {
        return fuzzySearch;
    }

    public void setFuzzySearch(boolean fuzzySearch) {
        this.fuzzySearch = fuzzySearch;
    }
}
