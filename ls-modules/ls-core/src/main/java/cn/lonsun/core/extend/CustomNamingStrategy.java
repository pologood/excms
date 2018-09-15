package cn.lonsun.core.extend;


import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.springframework.stereotype.Component;

/**
 * 
 * <自动映射实体属性与字段>：<br/>
 * <功能详细描述>：
 * @author zhusy
 * @Time 2014年8月14日 下午4:59:30
 */
@Component("customNamingStrategy")
public class CustomNamingStrategy extends ImprovedNamingStrategy implements
		NamingStrategy {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 自定义字段名(字段名为属性名按照不同单词以下划线规则分割规则生成)
	 */
    @Override
    public String propertyToColumnName(String propertyName) {
        return addUnderscores(propertyName).toUpperCase();
    }

}
