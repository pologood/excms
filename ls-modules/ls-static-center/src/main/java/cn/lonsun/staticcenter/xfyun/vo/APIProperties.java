package cn.lonsun.staticcenter.xfyun.vo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * 讯飞语音api参数设置
 * Created by huangxx on 2018/7/4.
 */
@Repository("apiProperties")
public class APIProperties {

    @Value("${xfyy.api.id}")
    private String API_ID;

    @Value("${xfyy.api.key}")
    private String API_KEY;

    @Value("${xfyy.api.url}")
    private String API_URL;

    public String getAPI_ID() {
        return API_ID;
    }

    public void setAPI_ID(String API_ID) {
        this.API_ID = API_ID;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public void setAPI_URL(String API_URL) {
        this.API_URL = API_URL;
    }
}
