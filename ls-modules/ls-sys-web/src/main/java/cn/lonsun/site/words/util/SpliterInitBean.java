package cn.lonsun.site.words.util;

import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author gu.fei
 * @version 2015-10-20 16:51
 */
public class SpliterInitBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化分词器
        WordsSplitHolder.init();
    }
}
