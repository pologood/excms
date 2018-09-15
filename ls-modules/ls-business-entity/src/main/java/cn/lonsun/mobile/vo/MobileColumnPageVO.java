package cn.lonsun.mobile.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author Doocal
 * @ClassName: MobileColumnPageVO
 * @Description: 限制IP分页
 */
public class MobileColumnPageVO extends PageQueryVO {

    //ID
    private Long id;

    //名称
    private String name;

    private String url;

    private String img;

    //对应系统栏目类型
    private String type;

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
}
