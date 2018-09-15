package cn.lonsun.lottery;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.service.ILotteryAnswerService;
import cn.lonsun.lottery.internal.service.ILotteryQuestionsService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-13.
 */
@Controller
@RequestMapping(value = "/lotteryAnswer")
public class LotteryAnswerController extends BaseController {
    @Autowired
    private ILotteryAnswerService lotteryAnswerService;
    @Autowired
    private ILotteryQuestionsService lotteryQuestionsService;



    @RequestMapping(value = "/answerIndex")
    public String answerIndex() {
        return  "/lottery/answer_index";
    }

    @RequestMapping(value = "/getAnswers")
    @ResponseBody
    public Object getAnswers(QuestionsQueryVO vo){
        Pagination pagination = lotteryAnswerService.getAnswers(vo);
        return getObject(pagination);
    }



    @RequestMapping(value = "/editAnswer")
    public String editAnswer(Model model,Long answerId ) {
        model.addAttribute("answerId",answerId);
        return  "/lottery/edit_Answer";
    }



    @RequestMapping(value = "/getOptions")
    @ResponseBody
    public Object getOptions(){
        List<DataDictVO> dictVOs = DataDictionaryUtil.getDDList("answer_option");
        return getObject(dictVOs);
    }


    @RequestMapping(value = "/getQuestions")
    @ResponseBody
    public Object getQuestions(){

        Map<String,Object>  param=new HashMap<String,Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        param.put("siteId", LoginPersonUtil.getSiteId());
         List<LotteryQuestionsEO> list = lotteryQuestionsService.getEntities(LotteryQuestionsEO.class, param);



        return getObject(list);
    }

    @RequestMapping(value = "/getAnswerById")
    @ResponseBody
    public Object getAnswerById(Long answerId){
        LotteryAnswerEO  lotteryAnswerEO =new LotteryAnswerEO();
        if(!AppUtil.isEmpty(answerId)){

            lotteryAnswerEO=lotteryAnswerService.getEntity(LotteryAnswerEO.class,answerId);
        }

        return getObject(lotteryAnswerEO);
    }


    @RequestMapping(value = "/saveAnswer")
    @ResponseBody
    public Object saveAnswer(LotteryAnswerEO lotteryAnswerEO){
        if(AppUtil.isEmpty(lotteryAnswerEO.getAnswerOption())){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"选项不能为空");
        }
        if(AppUtil.isEmpty(lotteryAnswerEO.getLotteryId())){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"答题不能为空");
        }

        List<LotteryAnswerEO> list = lotteryAnswerService.checkOption(lotteryAnswerEO);
        if(null!=list&&list.size()>0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"该答题已存在选项"+lotteryAnswerEO.getAnswerOption());

        }

        lotteryAnswerService.saveAnswer(lotteryAnswerEO);
        return getObject(lotteryAnswerEO);
    }


    @RequestMapping(value = "/delAnswers")
    @ResponseBody
    public Object delAnswers(Long[] ids){
        if(null==ids||ids.length<=0){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待删除项!");

        }
       List<LotteryAnswerEO> list = lotteryAnswerService.getEntities(LotteryAnswerEO.class, ids);

        for( LotteryAnswerEO eo:  list ){
            if(eo.getIsTrue().intValue()==1){
                LotteryQuestionsEO lotteryQuestionsEO =  lotteryQuestionsService.getEntity(LotteryQuestionsEO.class, eo.getLotteryId());
                lotteryQuestionsEO.setAnswerId(null);
                lotteryQuestionsService.updateEntity(lotteryQuestionsEO);

            }

        }
        lotteryAnswerService.delete(list);

        return getObject();
    }




}
