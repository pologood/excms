package cn.lonsun.indicator.internal.entity;

import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-9-24 10:17
 */
public class FunctionEO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;

    private String name;

    private boolean checked;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
