/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.core.util;

import com.alibaba.fastjson.serializer.PropertyFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * 通过指定类+属性来限制输出的JSON
 * @author Dzl
 */
public class JSONSimplePropertyFilter implements PropertyFilter {

    private Map<Class<?>, Set<String>> map = new HashMap<Class<?>, Set<String>>();
    private Boolean isInclude=true;   
    public JSONSimplePropertyFilter() {
    }

    public JSONSimplePropertyFilter(Map<Class<?>, Set<String>> map) {
        this.map = map;
    }
    public JSONSimplePropertyFilter(Map<Class<?>, Set<String>> map,boolean isInclude) {
        this.map = map;
        this.isInclude = isInclude;
    }

    /**
     * 过滤不需要被序列化的属性，主要是应用于Hibernate的代理和管理。
     *
     * @param source 属性所在的对象
     * @param name 属性名
     * @param value 属性值
     * @return 返回false属性将被忽略，ture属性将被保留
     */
    @Override
    public boolean apply(Object source, String name, Object value) {
        if (value instanceof HibernateProxy) {//hibernate代理对象
            LazyInitializer initializer = ((HibernateProxy) value).getHibernateLazyInitializer();
            if (initializer.isUninitialized()) {
                return false;
            }
        } else if (value instanceof PersistentCollection) {//实体关联集合一对多等
            PersistentCollection collection = (PersistentCollection) value;
            if (!collection.wasInitialized()) {
                return false;
            }
            Object val = collection.getValue();
            if (val == null) {
                return false;
            }
        }
        for(Entry<Class<?>, Set<String>> entry : map.entrySet()) {  
            Class<?> class1 = entry.getKey();
            if(source.getClass() == class1){  
                Set<String> fields = entry.getValue();  
                for(String field : fields) {  
                    if(field.equals(name)){  
                        return this.isInclude;  
                    }  
                }  
            }  
        }  
        if(AjaxObj.class==source.getClass())return true;
        return !this.isInclude;
    }
}
