package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IBbsFileService extends IBaseService<BbsFileEO> {

	/**
	 * 获取论坛附件分页
	 *
	 */
	public Pagination getPage(FileCenterVO fileVO);

	void deleteFiles(Long[] ids, Integer isDel);

	boolean removeFromDir(Long[] ids);

	/**
	 *
	 * @param ids 附件主键
	 * @param auditStatus  审核状态
	 * @param caseId   外键id
	 * @param postId   主题id
     * @param plateId  版块id
     */
	void setFilesStatus(Long[] ids, Integer auditStatus, Long caseId, Long postId, Long plateId);

	List<BbsFileEO> getBbsFiles(Long caseId);

	BbsFileEO fileUpload(MultipartFile filedata, HttpServletRequest request, BbsFileEO file);

	/**
	 * 根据外键删除附件
	 * @param caseIds
	 * @param isDel
     */
	void deleteBycaseId(Long[] caseIds, Integer isDel);

	/**
	 * 还原附件
	 * @param caseIds
     */
	void restoreFiles(Long[] caseIds);

	/**
	 * 更新附件所有版块
	 * @param bbsPost
     */
	void updateFilesPlateId(BbsPostEO bbsPost);

	/**
	 * 更新附件审核状态
	 * @param postId
	 * @param i
     */
	void setFilesAuditStatus(Long[] postId, Integer audit);

	Integer getFileSuffix(Long[] fileIds);

	void updatePostFilesSuffix(Long postId);

	void deleteWebFiles(Long[] ids);
}
