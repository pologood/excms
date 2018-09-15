package cn.lonsun.content.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.dao.IContentPicDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.vo.SynColumnVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.*;

//import net.sf.json.JSONArray;

/**
 * 
 * @ClassName: ContentPicServiceImpl
 * @Description: 新闻图片业务逻辑
 * @author Hewbing
 * @date 2015年10月15日 上午11:27:15
 *
 *
 */
@Service("contentPicService")
public class ContentPicServiceImpl extends MockService<ContentPicEO> implements
		IContentPicService {
	@Autowired
	private IContentPicDao contentPicDao;
	@Autowired
	private IBaseContentService baseContentService;
	@Autowired
	private ContentMongoServiceImpl contentMongoService;
	@DbInject("baseContent")
	private IBaseContentDao baseContentDao;
    @Autowired
    private IContentReferRelationService contentReferRelationService;
	@Override 
	public List<ContentPicEO> getPicsList(Long contentId) {
		return contentPicDao.getPicsList(contentId);
	}
	@Override
	public void changePicPath(String path,String thumbPath, Long picId) {
		contentPicDao.updatePicPath(path,thumbPath, picId);
	}
	@Override
	public void updatePicInfo(ContentPicEO picEO) {
		contentPicDao.updatePicInfo(picEO);
	}
	@Override
	public void allSavePic(List<ContentPicEO> picList) {
		for(ContentPicEO l:picList){
			contentPicDao.updatePic(l);
		}
	}
	@Override
	public void addPic(ContentPicEO picEO) {
		contentPicDao.save(picEO);
	}
	@SuppressWarnings("unchecked")
	@Override
	public SynColumnVO savePicNews(BaseContentEO contentEO,String content, String picList,Long[] synColumnIds) {
		Long contentId=null;
		List<ContentPicEO> list = new ArrayList<ContentPicEO>();
		if(!AppUtil.isEmpty(picList)){
			picList=picList.replace("\\n", "\n");//文本框的换行转换
		}
		SynColumnVO _vo=new SynColumnVO();
		List<Long> picIds=new ArrayList<Long>();
		if(!AppUtil.isEmpty(picList)){
			JSONArray json = JSONArray.fromObject(picList);
			list = (List<ContentPicEO>) JSONArray.toCollection(json, ContentPicEO.class);
		}
		if(synColumnIds!=null&&synColumnIds.length>0){
			contentEO.setQuoteStatus(2);
		}
		//获取时间戳充当排序号，修改新闻后
		Date publishDate = contentEO.getPublishDate();
		if(publishDate==null){
			publishDate = new Date();
		}
		Long sort = publishDate.getTime()/1000;

		if(null!=contentEO.getId()){
			contentId=contentEO.getId();

			BaseContentEO newEO = baseContentDao.getEntity(BaseContentEO.class, contentEO.getId());
			if(newEO.getPublishDate()==null||publishDate.getTime()!=newEO.getPublishDate().getTime()){//修改新闻时若发布时间发生变化，则重新生成排序号
				contentEO.setNum(sort);
			}

			if(contentEO.getIsPublish()==1){
				contentEO.setIsPublish(2);//设置为中间状态“发布中”
			}else {
				if(null!=newEO&&newEO.getIsPublish()==1){//取消发布的也需要将发布状态设置为中间状态“发布中”
					contentEO.setIsPublish(2);//设置为中间状态“发布中”
				}
			}
			baseContentDao.merge(contentEO);

			CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO); 
			SysLog.log("修改图片新闻：栏目（"+ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"），标题（"+contentEO.getTitle()+"）", "BaseContentEO", CmsLogEO.Operation.Update.toString());
		}else{
			if(synColumnIds!=null&&synColumnIds.length>0) contentEO.setQuoteStatus(2);
//			SortVO _svo=baseContentService.getMaxNumByColumn(contentEO.getColumnId());
//			Long sort=1L;
//			if(!AppUtil.isEmpty(_svo.getSortNum())) sort=_svo.getSortNum()+1L;
			contentEO.setNum(sort);

			if(!AppUtil.isEmpty(contentEO.getEditor())&&"导入".equals(contentEO.getEditor())){//导数据时不做处理

			}else{
				if(contentEO.getIsPublish()==1){
					contentEO.setIsPublish(2);//设置为中间状态“发布中”
				}
			}

			contentId=baseContentService.saveEntity(contentEO);

			CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO); 
			SysLog.log("新增图片新闻：栏目（"+ColumnUtil.getColumnName(contentEO.getColumnId(),contentEO.getSiteId())+"），标题（"+contentEO.getTitle()+"）", "BaseContentEO", CmsLogEO.Operation.Add.toString());
			
		}
		if(contentEO.getQuoteStatus()==0){
			FileUploadUtil.markByContentIds(new Long[]{contentId},0);
		}else {
        	List<ContentReferRelationEO> referList = contentReferRelationService.getByCauseId(contentId, ContentReferRelationEO.ModelCode.CONTENT.toString(),ContentReferRelationEO.TYPE.REFER.toString());
        	for(ContentReferRelationEO rl:referList){
        		BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, rl.getReferId());
        		eo.setTitle(contentEO.getTitle());
        		baseContentService.updateEntity(eo);
        	}
        }
		FileUploadUtil.setStatus(contentEO.getImageLink(), 1, contentId,contentEO.getColumnId(),contentEO.getSiteId());
		if(null!=contentId){
			ContentMongoEO _eo=new ContentMongoEO();
			_eo.setId(contentId);
			_eo.setContent(content);
			contentMongoService.save(_eo);
		}
		List<String> mongoIds=new ArrayList<String>();
		if(list!=null&&list.size()>0){
			for(ContentPicEO l:list){
				contentPicDao.updatePic(l,contentId);
				ContentPicEO _eo=getEntity(ContentPicEO.class, l.getPicId());
				if(!AppUtil.isEmpty(_eo)){
					mongoIds.add(_eo.getPath());
					mongoIds.add(_eo.getThumbPath());
				}
				picIds.add(l.getPicId());
			}
			int size =  list.size();
			String[] ids=(String[]) mongoIds.toArray(new String[size]);
			FileUploadUtil.setStatus(ids, 1, contentId,contentEO.getColumnId(),contentEO.getSiteId());;
		}
		
		_vo.setPicList(picIds);
		_vo.setContentId(contentId);
		return _vo;
	}
	
	@Override
	public List<ContentPicEO> getListByPath(String[] paths) {
		return contentPicDao.getListByPath(paths);
	}
	@Override
	public String picBeautify(MultipartFile Filedata, Long siteId,
			Long columnId, Long contentId, Long picId,
			HttpServletRequest request) {
		
		String filePath = "";
		String thumbPath = "";
		String picName = Filedata.getOriginalFilename();
		String suffix = picName.substring(picName.lastIndexOf(".") + 1,
				picName.length()).toLowerCase();
		try {
			//path = fileServer.uploadMultipartFile(Filedata, suffix);
			byte[] bt = WaterMarkUtil.createWaterMark(Filedata.getInputStream(), siteId,suffix);
			MongoFileVO mgvo = FileUploadUtil.uploadUtil(bt,picName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.PicNewsUpload.toString(), siteId, columnId, contentId, "图片新闻美图美化的图片", request);
			filePath=mgvo.getMongoId();
			if ("gif".equals(suffix)) {
				thumbPath = filePath;
			} else {
				// 生成缩略图
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImgHander.ImgTrans(Filedata.getInputStream(), 125, 0,baos);
				byte[] b = baos.toByteArray();
				//thumbPath = fileServer.uploadFile(b, suffix);
				MongoFileVO _vo = FileUploadUtil.uploadUtil(b, picName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.PicNewsUpload.toString(), siteId, columnId, contentId, "图片新闻美图美化的图片缩略图", request);
				thumbPath=_vo.getMongoId();
				baos.flush();
				baos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseRunTimeException(TipsMode.Message.toString(), "失败");
		}
		ContentPicEO _eo=contentPicDao.getEntity(ContentPicEO.class, picId);
		FileUploadUtil.setStatus(new String[]{_eo.getPath(),_eo.getThumbPath()},0);
		contentPicDao.updatePicPath(filePath, thumbPath, picId);
		FileUploadUtil.setStatus(new String[]{filePath,thumbPath},1);
		return thumbPath;
	}
	@Override
	public void delPic(Long picId) {
		ContentPicEO _eo=contentPicDao.getEntity(ContentPicEO.class, picId);
		FileUploadUtil.setStatus(new String[]{_eo.getPath(),_eo.getThumbPath()},0);
		delete(ContentPicEO.class, picId);
	}
	@Override
	public void synToColumn(List<Long> picIds, BaseContentEO contentEO,String content,
			Long[] synColumnIds,Long oldId) {

		for(int i=0;i<synColumnIds.length;i++){
			
			BaseContentEO newEO=new BaseContentEO();
			try {
				BeanUtils.copyProperties(newEO, contentEO);
				newEO.setQuoteStatus(2);
				newEO.setIsNew(0);
				newEO.setIsTop(0);
				newEO.setIsPublish(0);
				newEO.setIsTitle(0);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BaseRunTimeException(TipsMode.Message.toString(), "复制信息到栏目出错");
			}
			
			newEO.setColumnId(synColumnIds[i]);
			Long _id=baseContentService.saveEntity(newEO);
			CacheHandler.saveOrUpdate(BaseContentEO.class, newEO); 
			if(null!=_id){
				ContentMongoEO _eo=new ContentMongoEO();
				_eo.setId(_id);
				_eo.setContent(content);
				contentMongoService.save(_eo);
			}
			for(Long picId:picIds){
				ContentPicEO _newPic=new ContentPicEO();
				ContentPicEO _picEO=getEntity(ContentPicEO.class, picId);
				try {
					BeanUtils.copyProperties(_newPic, _picEO);
				} catch (Exception e) {
					e.printStackTrace();
					throw new BaseRunTimeException(TipsMode.Message.toString(), "复制信息到栏目出错");
				}
				_newPic.setColumnId(synColumnIds[i]);
				_newPic.setContentId(_id);
				_newPic.setPicId(null);
				saveEntity(_newPic);
			}
            ContentReferRelationEO relationEO=new ContentReferRelationEO();
            relationEO.setCauseById(oldId);
            relationEO.setModelCode("CONTENT");
            relationEO.setColumnId(synColumnIds[i]);
            relationEO.setReferId(_id);
            contentReferRelationService.saveEntity(relationEO);
			
		}
	}
	@Override
	public void removePic(Long[] contentIds) {
		contentPicDao.removePic(contentIds);
	}

	@Override
	public void updatePic(ContentPicEO l, Long id) {
		contentPicDao.updatePic(l,id);
	}


	@Override
	public  void updateNums(Long picIds[],Long sortNums[]){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("picId", picIds);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<ContentPicEO> list = getEntities(ContentPicEO.class, params);
		if (null != list && list.size() > 0) {
			Map<Long, ContentPicEO> contentMap = (Map<Long, ContentPicEO>) AppUtil.parseListToMap(list, "picId");
			ContentPicEO eo = null;
			for (int i = 0, l = picIds.length; i < l; i++) {
				if (null != (eo = contentMap.get(picIds[i]))) {
					eo.setNum(sortNums[i]);
				}
			}
			updateEntities(list);
		}
	}


}
