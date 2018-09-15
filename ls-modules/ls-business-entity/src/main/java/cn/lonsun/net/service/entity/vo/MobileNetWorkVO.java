package cn.lonsun.net.service.entity.vo;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-6-29 14:06
 */
public class MobileNetWorkVO {

    private Long id;

    private String title;

    private String url;

    private String date;

    private String img;

    private Long parentId;

    private List<MobileNetWorkVO> mobileNetWorkVOs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<MobileNetWorkVO> getMobileNetWorkVOs() {
        return mobileNetWorkVOs;
    }

    public void setMobileNetWorkVOs(List<MobileNetWorkVO> mobileNetWorkVOs) {
        this.mobileNetWorkVOs = mobileNetWorkVOs;
    }
}
