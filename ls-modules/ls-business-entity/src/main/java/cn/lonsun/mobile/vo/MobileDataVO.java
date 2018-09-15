package cn.lonsun.mobile.vo;

import java.util.Date;
import java.util.List;

/**
 * @author Doocal
 * @ClassName: MobileColumnPageVO
 * @Description: 限制IP分页
 */
public class MobileDataVO {

    //ID
    private Long id;

    //名称
    private String name;

    //绝对地址
    private String url;

    //缩略图
    private String img;

    //对应该APP前端区块
    private String type;

    //对应系统栏目类型
    private String code;

    //自定义导航选中
    private Boolean isChecked;

    //栏目ID
    private Long indicatorId;

    //创建日期
    private Date date;

    private Integer orderType=1;

    private Integer isReply=1;

    private List<MobileDataVO> data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<MobileDataVO> getData() {
        return data;
    }

    public void setData(List<MobileDataVO> data) {
        this.data = data;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getIsReply() {
        return isReply;
    }

    public void setIsReply(Integer isReply) {
        this.isReply = isReply;
    }
}
