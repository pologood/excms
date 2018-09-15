package cn.lonsun.core.util;

import java.io.Serializable;

/**
 * @author Doocal
 */
public class UeditorJSON implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7133144256777652839L;
    private String state;
    private String url;
    private String title;
    private String original;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public UeditorJSON() {
    }

    public static UeditorJSON Info(String state, String title, String url, String original) {
        UeditorJSON el = new UeditorJSON();
        el.setState(state);
        el.setTitle(title);
        el.setUrl(url);
        el.setOriginal(original);
        return el;
    }

}
