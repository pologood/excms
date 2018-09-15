package cn.lonsun.webservice;

import cn.lonsun.webservice.to.WebServiceTO;

/**
 * Created by fth on 2016/11/2.
 */
public interface PublicClassService {

    /**
     * 获得信息公开所属分类列表
     *
     * @return
     */
    WebServiceTO getPublicClassList();
}
