package cn.lonsun.content.survey.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyReplyService;
import cn.lonsun.content.survey.util.MapUtil;
import cn.lonsun.content.survey.vo.QuestionOptionsVO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.util.FileUploadUtil;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping(value = "/survey/question", produces = {"application/json;charset=UTF-8"})
public class SurveyQuestionController extends BaseController {

    @Autowired
    private ISurveyQuestionService surveyQuestionService;

    @Autowired
    private ISurveyOptionsService surveyOptionsService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private ISurveyReplyService surveyReplyService;


    @RequestMapping("list")
    public String list(Long themeId, Integer options, Model m) {
        m.addAttribute("themeId", themeId);
        m.addAttribute("options", options);
        return "/content/survey/question_list";
    }

    @RequestMapping("edit")
    public String edit(Long questionId, Long themeId, Model m) {
        m.addAttribute("themeId", themeId);
        m.addAttribute("questionId", questionId);
        return "/content/survey/question_edit";
    }

    @RequestMapping("editOptions")
    public String editOptions(Long optionId, Long questionId, Long themeId, Model m) {
        m.addAttribute("optionId", optionId);
        m.addAttribute("themeId", themeId);
        m.addAttribute("questionId", questionId);
        return "/content/survey/option_edit";
    }

    @RequestMapping("replyList")
    public String replyList(Long questionId, Model m) {
        m.addAttribute("optionId", questionId);
        return "/content/survey/question_replyList";
    }

    @RequestMapping("replyEdit")
    public String replyEdit(Long replyId, Model m) {
        m.addAttribute("replyId", replyId);
        return "/content/survey/question_replyEdit";
    }

    @RequestMapping("getReplyPage")
    @ResponseBody
    public Object getReplyPage(SurveyQueryVO query) {
        if (query.getQuestionId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "QuestionId不能为空");
        }
        // 页码与查询最多查询数据量纠正
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        Pagination page = surveyReplyService.getPage(query);
        return getObject(page);
    }

    @RequestMapping("getList")
    @ResponseBody
    public Object getList(SurveyQueryVO query) {
        if (query.getThemeId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "themeId不能为空");
        }
        List<QuestionOptionsVO> qoList = new ArrayList<QuestionOptionsVO>();
        //获取所有问题
        List<SurveyQuestionEO> questions = surveyQuestionService.getListByThemeId(query.getThemeId());
        if (questions != null && questions.size() > 0) {
            QuestionOptionsVO question = null;
            QuestionOptionsVO option = null;
            //获取所有选项
            List<SurveyOptionsEO> options = surveyOptionsService.getListByThemeId(query.getThemeId());
            //获取所有回复
            List<SurveyReplyEO> replys = surveyReplyService.getListByThemeId(query.getThemeId(), null);
            //处理选项
            Map<Long, List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(options);
            Map<Long, List<SurveyReplyEO>> replysMap = MapUtil.getReplysMap(replys);
            for (SurveyQuestionEO sq : questions) {
                question = new QuestionOptionsVO();
                question.setIdField("question_" + sq.getQuestionId());// 主键
                question.setParentField("0");// 父键
                question.setId(sq.getQuestionId());
                question.setTitle(sq.getTitle());
                question.setPid(0L);
                question.setOptions(sq.getOptions());
                question.setOptionsCount(sq.getOptionsCount());
                question.setType(QuestionOptionsVO.Type.Question.toString());
                if (sq.getOptions() == 3) {
                    //不是文本时
                    List<SurveyReplyEO> replysList = (replysMap == null ? null : replysMap.get(sq.getQuestionId()));
                    Integer count = (replysList == null ? 0 : replysList.size());
                    question.setVotesCount(count.longValue());
                } else {
                    Long count = 0L;
                    //不是文本时
                    List<SurveyOptionsEO> replysList = (optionsMap == null ? null : optionsMap.get(sq.getQuestionId()));
                    if (replysList != null && replysList.size() > 0) {
                        for (int i = 0; i < replysList.size(); i++) {
                            option = new QuestionOptionsVO();
                            option.setIdField("option_" + replysList.get(i).getOptionId());
                            option.setParentField("question_" + sq.getQuestionId());
                            option.setId(replysList.get(i).getOptionId());
                            option.setPid(sq.getQuestionId());
                            option.setTitle(replysList.get(i).getTitle());
                            option.setVotesCount(replysList.get(i).getVotesCount());
                            option.setType(QuestionOptionsVO.Type.Option.toString());
                            Long votesCount = replysList.get(i).getVotesCount();
                            count += (votesCount == null ? 0 : votesCount);
                            qoList.add(option);
                        }
                    }
                    question.setVotesCount(count);
                }
                qoList.add(question);
            }
        }
        return getObject(qoList);
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(SurveyQueryVO query) {

        // 页码与查询最多查询数据量纠正
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        Pagination page = surveyQuestionService.getPage(query);
        return getObject(page);
    }

    @RequestMapping("save")
    @ResponseBody
    public Object save(SurveyQuestionEO surveyQuestion) {
        if (surveyQuestion.getQuestionId() != null) {
            surveyQuestionService.updateEntity(surveyQuestion);
        } else {
            surveyQuestionService.saveEntity(surveyQuestion);
        }
        return getObject();
    }

    @RequestMapping("getSurveyQuestion")
    @ResponseBody
    public Object getSurveyQuestion(Long id) {
        SurveyQuestionEO surveyQuestion = null;
        if (id == null) {
            surveyQuestion = new SurveyQuestionEO();
        } else {
            surveyQuestion = surveyQuestionService.getEntity(SurveyQuestionEO.class, id);
        }
        return getObject(surveyQuestion);
    }

    @RequestMapping("getOption")
    @ResponseBody
    public Object getOption(Long optionId, Long themeId) {
        SurveyOptionsEO surveyoptions = null;
        if (optionId == null) {
            surveyoptions = new SurveyOptionsEO();
        } else {
            surveyoptions = surveyOptionsService.getEntity(SurveyOptionsEO.class, optionId);
        }
        return getObject(surveyoptions);
    }

    @RequestMapping("getReply")
    @ResponseBody
    public Object getReply(Long replyId) {
        SurveyReplyEO surveyReply = null;
        if (replyId == null) {
            surveyReply = new SurveyReplyEO();
        } else {
            surveyReply = surveyReplyService.getEntity(SurveyReplyEO.class, replyId);
        }
        return getObject(surveyReply);
    }

    @RequestMapping("saveOption")
    @ResponseBody
    public Object saveOption(SurveyOptionsEO surveyOptions) {
        if (surveyOptions.getOptionId() != null) {
            surveyOptionsService.updateEntity(surveyOptions);
        } else {
            surveyOptionsService.saveEntity(surveyOptions);
        }
        if (!StringUtils.isEmpty(surveyOptions.getPicUrl())) {
            FileUploadUtil.saveFileCenterEO(surveyOptions.getPicUrl());
        }
        return getObject();
    }

    @RequestMapping("saveReply")
    @ResponseBody
    public Object saveReply(SurveyReplyEO surveyReply, HttpServletRequest request) {
        if (surveyReply.getReplyId() != null) {
            surveyReplyService.updateEntity(surveyReply);
        } else {
            surveyReply.setIp(RequestUtil.getIpAddr(request));
            surveyReplyService.saveEntity(surveyReply);
        }
        return getObject();
    }

    @RequestMapping("updateReplyStatus")
    @ResponseBody
    public Object updateOptionStatus(@RequestParam("ids") Long[] ids,
                                     Integer status) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                SurveyReplyEO surveyReply = surveyReplyService.getEntity(SurveyReplyEO.class, id);
                if (surveyReply != null && surveyReply.getIsIssued() != status) {
                    if (status == 1) {
                        surveyReply.setIssuedTime(new Date());
                    } else {
                        surveyReply.setIssuedTime(null);
                    }
                    surveyReply.setIsIssued(status);
                    surveyReplyService.updateEntity(surveyReply);
                }
            }
        }
        return getObject();
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids") Long[] ids) {
        try {
            surveyQuestionService.delete(ids);
        } catch (Exception e) {
            throw new BaseRunTimeException();
        }
        return getObject();
    }

    @RequestMapping("deleteReply")
    @ResponseBody
    public Object deleteReply(@RequestParam("ids") Long[] ids) {
        try {
            surveyReplyService.delete(SurveyReplyEO.class, ids);
        } catch (Exception e) {
            throw new BaseRunTimeException();
        }
        return getObject();
    }

    @RequestMapping("deleteOption")
    @ResponseBody
    public Object deleteOption(Long optionId) {
        try {
            SurveyOptionsEO surveyOptions = surveyOptionsService.getEntity(SurveyOptionsEO.class, optionId);
            if (!StringUtils.isEmpty(surveyOptions.getPicUrl())) {
                FileUploadUtil.deleteFileCenterEO(surveyOptions.getPicUrl());
            }
            surveyOptionsService.delete(SurveyOptionsEO.class, optionId);

        } catch (Exception e) {
            throw new BaseRunTimeException();
        }
        return getObject();
    }

    @RequestMapping("upload")
    public void upload(@RequestParam("file") MultipartFile file, String uploadType, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        //		if(!StringUtils.isEmpty(uploadType)){
        //			String fileName = file.getOriginalFilename();
        //			 Pattern reg=Pattern.compile("[.]jpg|png|jpeg|gif$");
        //		        Matcher matcher=reg.matcher(fileName);
        //		        if(!matcher.find()) {
        //		        }
        //		}

        MongoFileVO mongvo = mongoDbFileServer.uploadMultipartFile(file, null);
        response.setContentType("text/html");
        response.getWriter().write(JSON.toJSONString(ajaxOk(mongvo)));
    }
}
