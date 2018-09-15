package cn.lonsun.system.filecenter.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.system.filecenter.internal.dao.IFileCenterDao;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import cn.lonsun.util.LoginPersonUtil;

@Service("fileCenterService")
public class FileCenterServiceImpl extends MockService<FileCenterEO> implements IFileCenterService{
		@Autowired
		private IMongoDbFileServer mongoDbFileServer;
		
		@Autowired
		private IFileCenterDao fileCenterDao;
		@Override
		public void setStatus(Long[] ids, Integer status) {
			fileCenterDao.updateStatus(ids, status);
		}

		@Override
		public Pagination getFilePage(FileCenterVO fileVO) {
			if(AppUtil.isEmpty(fileVO.getSiteId())){
				fileVO.setSiteId(LoginPersonUtil.getSiteId());
			}
			return fileCenterDao.getFilePage(fileVO);
		}

		@Override
		public void setStatus(String[] mongoIds, Integer status) {
			fileCenterDao.updateStatus(mongoIds, status);;
		}

		@Override
		public void deleteByMongoId(String[] mongoIds) {
			fileCenterDao.deleteByMongoId(mongoIds);
		}

		@Override
		public boolean removeFromDir(Long[] ids) {
			try{
				List<FileCenterEO> list = getEntities(FileCenterEO.class, ids);
				List<Long> idList=new ArrayList<Long>();
				for(FileCenterEO li:list){
					if(li.getStatus()!=1){
						try{
						mongoDbFileServer.delete(li.getMongoId(), null);
						}catch(Exception e){
							idList.add(li.getId());
							e.printStackTrace();
						}
						idList.add(li.getId());
					}
				}
				int size=idList.size();
				Long[] rIds=idList.toArray(new Long[size]);
				delete(FileCenterEO.class, rIds);
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}

		
		
		@Override
		public void setFileEO(String[] mongoIds, Integer status, Long... ids) {
			fileCenterDao.updateFileEO(mongoIds, status, ids);
		}

		@Override
		public void markStatusByContentId(Long[] ids, Integer status) {
			fileCenterDao.updateByContentId(ids, status);
		}

		@Override
		public boolean cleanUp(Long[] ids) {
			try{
				List<FileCenterEO> list = getEntities(FileCenterEO.class, ids);
				List<Long> idList=new ArrayList<Long>();
				for(FileCenterEO li:list){
						if(li.getMongoId()==null){
							idList.add(li.getId());
						}else{
							mongoDbFileServer.delete(li.getMongoId(), null);
							idList.add(li.getId());
						}
				}
				int size=idList.size();
				Long[] rIds=idList.toArray(new Long[size]);
				delete(FileCenterEO.class, rIds);
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}
	public void doSave(FileCenterEO eo){
		fileCenterDao.doSave(eo);
	}

	@Override
	public FileCenterEO getByMongoName(String mongoname) {
		return fileCenterDao.getByMongoName(mongoname);
	}
}
