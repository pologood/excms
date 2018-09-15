package cn.lonsun.process.service.impl;

import cn.lonsun.base.DealStatus;
import cn.lonsun.base.ProcessBusinessType;
import cn.lonsun.common.fileupload.internal.enums.CaseType;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.form.IFormDataService;
import cn.lonsun.process.dao.IProcessFormDao;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.service.IProcessFormService;
import cn.lonsun.process.vo.FileEditVO;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-18.
 */
@Service
public class ProcessFormServiceImpl extends BaseService<ProcessFormEO> implements IProcessFormService {

    @Autowired
    private IProcessFormDao processFormDao;



    @Override
    public void saveDraft(Map<String, Object> paramMap) throws Exception{
        Long processFormId = AppUtil.getLong(paramMap.get("processFormId"));
        Long formId = AppUtil.getLong(paramMap.get("formId"));
        String commentFields = AppUtil.getValue(paramMap.get("commentFields"));
        String title = AppUtil.getValue(paramMap.get("taskTitle"));
        if(AppUtil.isEmpty(title)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"表单标题不能为空");
        }else{
            if(titleIsExist(processFormId,title)){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"表单标题已存在");
            }
        }
        ProcessFormEO processForm = null;
        if(null == processFormId){
            Long recordId = null;//contentAPI.saveForm(formId,paramMap);
            processForm = new ProcessFormEO();
            processForm.setTitle(title);
            processForm.setModuleCode(AppUtil.getValue(paramMap.get("moduleCode")));
            processForm.setProcessId(AppUtil.getLong(paramMap.get("processId")));
            //processForm.setFormStatus(ProcessFormEO.FormStatus.Draft.getValue());
            processForm.setCreatePersonName(AppUtil.getValue(paramMap.get("personName")));
            processForm.setCreateUnitId(AppUtil.getLong(paramMap.get("unitId")));
            saveEntity(processForm);
            saveAttach(AppUtil.getValue(paramMap.get("fileEditData")), processForm);
        }else{
            processForm = getEntity(ProcessFormEO.class,processFormId);
            //contentAPI.updateForm(processForm.getFormId(), paramMap, processForm.getRecordId(), commentFields);
            processForm.setTitle(title);
            updateEntity(processForm);
            updateAttach(AppUtil.getValue(paramMap.get("fileEditData")),processForm);
        }


    }


    private void saveAttach(String fileEditData,ProcessFormEO processForm){
        FileEditVO[] fileEditVOs = null;
        if (!AppUtil.isEmpty(fileEditData)) {
            fileEditVOs = Jacksons.json().fromJsonToObject(fileEditData, FileEditVO[].class);
            if (fileEditVOs != null && fileEditVOs.length > 0) {
                //更新附件,给附件设置所属实体ID
                Long[] fileIds = new Long[fileEditVOs.length];
                for (int i = 0, length = fileEditVOs.length; i < length; i++) {
                    fileIds[i] = fileEditVOs[i].getFileId();
                }
                //fileUploadService.setCaseId(processForm.getProcessFormId(), fileIds, true);
            }
        }

    }

    private void updateAttach(String fileEditData,ProcessFormEO processForm){
        FileEditVO[] fileEditVOs = null;
        if (!AppUtil.isEmpty(fileEditData)) {
            fileEditVOs = Jacksons.json().fromJsonToObject(fileEditData, FileEditVO[].class);
            if (fileEditVOs != null && fileEditVOs.length > 0) {
                /**
                 * 更新附件
                 */
                List<Long> newFileList = new ArrayList<Long>();//新增的文件
                List<Long> deleteFileList = new ArrayList<Long>();//删除的文件
                for (FileEditVO fileEditVO : fileEditVOs) {
                    if (FileEditVO.newFile.equals(fileEditVO.getStatus())){
                        newFileList.add(fileEditVO.getFileId());
                    }else if (FileEditVO.deleteFile.equals(fileEditVO.getStatus())){
                        deleteFileList.add(fileEditVO.getFileId());
                    }
                }
                if (newFileList.size() > 0) {
                    //this.fileUploadService.setFileUsed(newFileList);//新增的文件设置已使用
                }
                if (deleteFileList.size() > 0) {
                    //草稿状态表单的附件物理删除
                    /*if (processForm.getFormStatus().intValue() == ProcessFormEO.FormStatus.Draft.getValue()){
                        //this.fileUploadService.delete("", deleteFileList);
                    }else{
                        //this.fileUploadService.mockDelete(deleteFileList);
                    }*/
                }
                //this.fileUploadService.setIndex(CaseType.ProcessForm.toString(), processForm.getProcessFormId());//给附件设置序号
            }
        }

    }


    /**
     * 获取分页列表数据
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getPagination(ProcessFormQueryVO queryVO) {
        return processFormDao.getPagination(queryVO);
    }


    /**
     * 删除草稿
     * @param processFormIds
     */
    @Override
    public void deleteDrafts(Long[] processFormIds) {
        List<ProcessFormEO> list = getEntities(ProcessFormEO.class, processFormIds);
        if(null == list || list.size() == 0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"数据读取错误,请联系管理员");
        }
        for(ProcessFormEO eo : list){
            /*if(ProcessFormEO.FormStatus.Draft.getValue().equals(eo.getFormStatus())){
                //contentAPI.delete(eo.getFormId(),eo.getRecordId());//删除表单记录
                delete(eo);
            }*/
        }
    }


    @Override
    public ProcessFormEO saveProcessForm(Map<String, Object> paramMap, DealStatus dealStatus) throws Exception{
        Long processFormId = AppUtil.getLong(paramMap.get("processFormId"));
        ProcessFormEO processForm = null;
        FormReturnVO formReturnVO = null;
        if(null == processFormId){
            formReturnVO = saveFormData(paramMap,dealStatus);
            processForm = new ProcessFormEO();
            processForm.setTitle(formReturnVO.getTitle());
            processForm.setDataId(formReturnVO.getDataId());
            processForm.setProcessBusinessType(getProcessBusinessTypeEnum(AppUtil.getValue(paramMap.get("processBusinessType"))));
            processForm.setModuleCode(AppUtil.getValue(paramMap.get("moduleCode")));
            processForm.setProcessId(AppUtil.getLong(paramMap.get("processId")));
            processForm.setProcessName(AppUtil.getValue(paramMap.get("processName")));
            processForm.setCreatePersonName(AppUtil.getValue(paramMap.get("createPersonName")));
            processForm.setCreateUnitId(AppUtil.getLong(paramMap.get("unitId")));
            processForm.setColumnId(AppUtil.getLong(paramMap.get("columnId")));
            processForm.setColumnName(AppUtil.getValue(paramMap.get("columnName")));
            if(null != dealStatus){
                processForm.setFormStatus(dealStatus.getValue());
            }
            if(DealStatus.Completed.equals(dealStatus)){
                processForm.setCurActivityName("已办结");//设置当前活动名称
            }
            saveEntity(processForm);
        }else{
            processForm = getEntity(ProcessFormEO.class,processFormId);
            formReturnVO = saveFormData(paramMap,dealStatus);
            processForm.setTitle(formReturnVO.getTitle());
            if(null != dealStatus){
                processForm.setFormStatus(dealStatus.getValue());
            }
            if(DealStatus.Completed.equals(dealStatus)){
                processForm.setCurActivityName("已办结");//设置当前活动名称
            }
            updateEntity(processForm);
        }
        return processForm;
    }

    @Override
    public ProcessFormEO saveProcessFormProcessStart(Map<String, Object> paramMap) {
        String processBusinessType = AppUtil.getValue(paramMap.get("processBusinessType"));
        if(AppUtil.isEmpty(processBusinessType)) throw new BaseRunTimeException(TipsMode.Message.toString(),"流程业务类型为空");
        IFormDataService service = getServiceBean(processBusinessType);
        Long dataId = AppUtil.getLong(paramMap.get("dataId"));
        FormReturnVO formReturnVO = service.getFormData(dataId);
        if(null == formReturnVO){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"获取流程业务数据出错");
        }
        ProcessFormEO processForm = new ProcessFormEO();
        processForm.setTitle(formReturnVO.getTitle());
        processForm.setDataId(formReturnVO.getDataId());
        processForm.setProcessBusinessType(getProcessBusinessTypeEnum(AppUtil.getValue(paramMap.get("processBusinessType"))));
        processForm.setModuleCode(AppUtil.getValue(paramMap.get("moduleCode")));
        processForm.setProcessId(AppUtil.getLong(paramMap.get("processId")));
        processForm.setProcessName(AppUtil.getValue(paramMap.get("processName")));
        processForm.setCreatePersonName(AppUtil.getValue(paramMap.get("createPersonName")));
        processForm.setCreateUnitId(AppUtil.getLong(paramMap.get("unitId")));
        processForm.setFormStatus(DealStatus.Dealing.getValue());
        processForm.setColumnId(AppUtil.getLong(paramMap.get("columnId")));
        processForm.setColumnName(AppUtil.getValue(paramMap.get("columnName")));
        saveEntity(processForm);
        return processForm;
    }

    @Override
    public boolean titleIsExist(Long processFormId, String title) {
        return processFormDao.getTitleIsExist(processFormId,title);
    }

    @Override
    public void processTerminate(Long recordId, String reason) {
        ProcessFormEO processForm = getEntity(ProcessFormEO.class,recordId);
        processForm.setFormStatus(DealStatus.Terminate.getValue());
        processForm.setCurActivityName("已终止");
        updateEntity(processForm);
        String serviceBeanName = processForm.getProcessBusinessType().getServiceBeanName();
        IFormDataService service = SpringContextHolder.getBean(serviceBeanName);
        service.updateDealStatus(processForm.getDataId(),DealStatus.Terminate);
    }

    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus){
        String processBusinessType = AppUtil.getValue(paramMap.get("processBusinessType"));
        if(AppUtil.isEmpty(processBusinessType)) throw new BaseRunTimeException(TipsMode.Message.toString(),"流程业务类型为空");
        IFormDataService service = getServiceBean(processBusinessType);
        Long dataId = AppUtil.getLong(paramMap.get("dataId"));
        FormReturnVO formReturnVO = null == dataId ? service.saveFormData(paramMap,dealStatus) : service.updateFormData(dataId,paramMap,dealStatus);
        return formReturnVO;
    }

    public IFormDataService getServiceBean(String processBusinessType){
        ProcessBusinessType typeEnum = getProcessBusinessTypeEnum(processBusinessType);
        String serviceBeanName = typeEnum.getServiceBeanName();
        try {
            IFormDataService service = SpringContextHolder.getBean(serviceBeanName);
            return service;
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(),"获取流程业务类型对应的表单服务对象失败");
        }
    }

    public ProcessBusinessType getProcessBusinessTypeEnum(String processBusinessType){
        ProcessBusinessType typeEnum = null;
        try {
            typeEnum = ProcessBusinessType.valueOf(processBusinessType);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(),"流程业务类型转换枚举错误");
        }
        return typeEnum;
    }


}
