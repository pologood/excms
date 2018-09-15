
package cn.lonsun.content.vo;

import cn.lonsun.content.internal.entity.BaseContentEO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-18<br/>
 */

public class OrdinaryPageVO extends BaseContentEO{
    private String text;
    private String content;

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
