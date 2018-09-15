package cn.lonsun.msg.submit.entity.vo;

/**
 * @author gu.fei
 * @version 2015-11-30 11:01
 */
public class EmployParamVo {

    //文章信息IDs
    private String msgIds;

    //采集到的栏目
    private String columnIds;

    //采集到的栏目
    private String cSiteIds;

    //采集的标题
    private String title;

    //采集内容
    private String content;

    // 0:采用到对应栏目，1:采用到其他栏目 2:批量采用当前栏目 3:批量采用到其他栏目
    private Integer type;

    public String getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(String msgIds) {
        this.msgIds = msgIds;
    }

    public String getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds;
    }

    public String getcSiteIds() {
        return cSiteIds;
    }

    public void setcSiteIds(String cSiteIds) {
        this.cSiteIds = cSiteIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
