package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.EFallBackSetType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2015-1-9.
 */
public class FallbackVO {
    private EFallBackSetType fallbackSet;
    private List<Map<String,Object>> fallbackOptions = new ArrayList<Map<String, Object>>(0);

    public FallbackVO() {

    }

    public EFallBackSetType getFallbackSet() {
        return fallbackSet;
    }

    public void setFallbackSet(EFallBackSetType fallbackSet) {
        this.fallbackSet = fallbackSet;
    }

    public List<Map<String, Object>> getFallbackOptions() {
        return fallbackOptions;
    }

    public void setFallbackOptions(List<Map<String, Object>> fallbackOptions) {
        this.fallbackOptions = fallbackOptions;
    }

    public void addFallbackSet(Map<String, Object> map) {
        this.fallbackOptions.add(map);
    }
}
