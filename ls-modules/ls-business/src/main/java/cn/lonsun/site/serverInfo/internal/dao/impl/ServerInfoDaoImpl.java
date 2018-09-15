package cn.lonsun.site.serverInfo.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.site.serverInfo.internal.dao.IServerInfoDao;
import cn.lonsun.site.serverInfo.internal.entity.ServerInfoEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-08-01 9:14
 */
@Repository
public class ServerInfoDaoImpl extends MockDao<ServerInfoEO> implements IServerInfoDao {
}
