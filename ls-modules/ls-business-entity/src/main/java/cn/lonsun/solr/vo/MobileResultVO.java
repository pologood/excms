package cn.lonsun.solr.vo;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-1-13 9:46
 */
public class MobileResultVO {

    private String title; //索引标题 ：文章标题

    private String content; //内容 ：文章内容

    private String url;

    private String img;

    private Date date;//BaseContentEO 的站点createDate

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
