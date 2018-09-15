/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.search.internal.dao.ISearchResultDao;
import cn.lonsun.search.internal.entity.SearchResultEO;
import cn.lonsun.search.internal.service.ISearchResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author zhongjun
 * @createtime 2017-11-16 17:55:00
 */
@Service("searchResultService")
public class SearchResultServiceImpl extends BaseService<SearchResultEO> implements ISearchResultService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ISearchResultDao SearchResultDao;


    @Override
    public Pagination getPage(PageQueryVO pageinfo){
        return SearchResultDao.getPage(pageinfo);
    }

    @Override
    public void saveOrUpdate(SearchResultEO eo) {
        if(!AppUtil.isEmpty(eo.getId())){
            super.updateEntity(eo);
            return;
        }
        super.saveEntity(eo);
    }

    @Override
    public SearchResultEO getByID(long id) {
        return super.getEntity(SearchResultEO.class, id);
    }
	
	@Override
    public void delete(long id){
        super.delete(SearchResultEO.class, id);
    }

    @Transactional
    @Override
    public void delete(String[] id) {
       Long[] ids = new Long[id.length];
       for(int i = 0; i < ids.length;i++){
           ids[i] = Long.valueOf(id[i]);
       }
       SearchResultDao.delete(SearchResultEO.class, ids);
    }

}
