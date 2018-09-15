package cn.lonsun.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.internal.dao.IErrorContentDao;
import cn.lonsun.internal.entity.ErrorContentEO;
import org.springframework.stereotype.Repository;

/**
 * 导入失败的记录
 * @author zhongjun
 */
@Repository
public class ErrorContentDaoImpl extends MockDao<ErrorContentEO> implements IErrorContentDao {



}
