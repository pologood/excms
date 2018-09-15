package cn.lonsun.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.internal.dao.IErrorContentDao;
import cn.lonsun.internal.dao.IErrorPublicContentDao;
import cn.lonsun.internal.entity.ErrorPublicContentEO;
import org.springframework.stereotype.Repository;

/**
 * 导入失败的记录
 * @author zhongjun
 */
@Repository
public class ErrorPublicContentDaoImpl extends MockDao<ErrorPublicContentEO> implements IErrorPublicContentDao {



}
