package cn.lonsun.lottery;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.entity.LotteryRecordEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by lizhi on 2017-1-15.
 */
@Controller
@RequestMapping(value = "/lotteryDraw")
public class LotteryDrawController extends BaseController {
    @Autowired
    private ILotteryRecordService lotteryRecordService;
    @Autowired
    private ILotteryQuestionsService lotteryQuestionsService;
    @Autowired
    private ILotteryAnswerService lotteryAnswerService;
    @Autowired
    private ILotteryRewardRecordService lotteryRewardRecordService;




    @RequestMapping(value = "/drawNotice")
    public String drawNotice() {
        return  "/lottery/draw_notice";
    }

    @RequestMapping(value = "/drawIndex")
    public String drawIndex(String name,String phone,Model model) {
        model.addAttribute("name",name);
        model.addAttribute("phone",phone);
        return  "/lottery/draw_index";
    }

    @RequestMapping(value = "/drawForm")
    public String drawForm(String name,String phone,Model model,Long titleNum) {
        model.addAttribute("name",name);
        model.addAttribute("phone",phone);
        LotteryQuestionsEO questionsEO =getQuestion();
        model.addAttribute("eo",questionsEO);
        model.addAttribute("titleNum",titleNum);
        return  "/lottery/draw_form";
    }



    @RequestMapping(value = "/checkUser")
    @ResponseBody
    public Object checkUser(String name,String phone){
        if(AppUtil.isEmpty(name)||AppUtil.isEmpty(phone)){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"姓名和联系方式不能为空");
        }
         //今天是否参与抽奖
        Object object =  lotteryRecordService.checkTodayUser(name,phone,null);
        if(null!=object&&(Long)object>=3){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"您今天已答题次数已用完!");


        }
        Long titleNum =(Long)object+1;


        return getObject(titleNum);
    }








    public LotteryQuestionsEO getQuestion(){

        Map<String,Object> map =new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<LotteryQuestionsEO> questionsEOs = lotteryQuestionsService.getEntities(LotteryQuestionsEO.class, map);
        if(null==questionsEOs||questionsEOs.size()<=0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"活动尚未设置答题");
        }

        Random random =new Random();
        int  index= random.nextInt(questionsEOs.size());
        LotteryQuestionsEO questionsEO= questionsEOs.get(index);
        map.put("lotteryId",questionsEO.getLotteryId());
//        List<LotteryAnswerEO> answerEOs = lotteryAnswerService.getEntities(LotteryAnswerEO.class, map);
        List<LotteryAnswerEO> answerEOs=lotteryAnswerService.getAnsersByQuestion(questionsEO.getLotteryId());


        questionsEO.setAnswerEOs(answerEOs);
        return questionsEO;
    }



    @RequestMapping(value = "/subAnswer")
    @ResponseBody
    public Object subAnswer(String name,String phone,Model model,String answer,Long answerId, Long lotteryId ){
        if(AppUtil.isEmpty(name)||AppUtil.isEmpty(phone)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请返回首页填写个人信息!");
        }

        Object object =  lotteryRecordService.checkTodayUser(name,phone,null);
        if(null!=object&&(Long)object>=3){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"您今天已答题次数已用完!");


        }

        LotteryQuestionsEO lotteryQuestionsEO = lotteryQuestionsService.getEntity(LotteryQuestionsEO.class, lotteryId);
        LotteryRecordEO lotteryRecordEO =new LotteryRecordEO();
        lotteryRecordEO.setResults(1);
        if(!lotteryQuestionsEO.getAnswerId().equals(answerId)){
            lotteryRecordEO.setResults(0);
//            throw new BaseRunTimeException(TipsMode.Message.toString(),"对不起,回答错误!");

        }

        lotteryRecordEO.setName(name);
        lotteryRecordEO.setPhone(phone);
        LotteryAnswerEO answerEO =lotteryAnswerService.getEntity(LotteryAnswerEO.class, answerId);
        lotteryRecordEO.setAnswer(answerEO.getAnswerOption());
        lotteryRecordEO.setLotteryId(lotteryId);
        lotteryRecordEO.setLotteryTitle(lotteryQuestionsEO.getLotteryTitle());
        LotteryAnswerEO trueAnswerEO  =lotteryAnswerService.getEntity(LotteryAnswerEO.class,lotteryQuestionsEO.getAnswerId());
        lotteryRecordEO.setTrueAnswer(trueAnswerEO.getAnswerOption());
        lotteryRecordService.saveEntity(lotteryRecordEO);

        return getObject();

    }
    @RequestMapping(value = "/drawResult")
    public String drawResult(String name,String phone,Model model) {
        model.addAttribute("name",name);
        model.addAttribute("phone",phone);
        //当日存在答错题，不允许
        Object object =  lotteryRecordService.checkTodayUser(name,phone,0);
        model.addAttribute("error",(Long)object);
        if(null!=object&&(Long)object>0){
            model.addAttribute("error","很遗憾答错"+(Long)object+"题,不能参与抽奖");
            return  "/lottery/draw_fail";

        }else if(null!=object&&(Long)object<=0){
            model.addAttribute("info","恭喜您全部回答正确获取一次抽奖机会去试试手气吧。");

        }



        return  "/lottery/draw_result";
    }

    @RequestMapping(value = "/drawPage")
    public String drawPage(String name,String phone,Model model) {
        model.addAttribute("name",name);
        model.addAttribute("phone",phone);
        if(AppUtil.isEmpty(name)||AppUtil.isEmpty(phone)){
           model.addAttribute("error","姓名和联系方式不能为空");
            return  "/lottery/draw_fail";
        }
        //当日存在答错题，不允许
        Object object =  lotteryRecordService.checkTodayUser(name,phone,0);
        if(null!=object&&(Long)object>0){
            model.addAttribute("error","很遗憾，请明天继续加油!");
            return  "/lottery/draw_fail";
        }
        //当日已抽过，不允许
        object =  lotteryRewardRecordService.checkTodayUser(name,phone);
        if(null!=object&&(Long)object>0){
            model.addAttribute("error","今日已参与抽奖!");
            return  "/lottery/draw_fail";
        }

        //每日抽奖金额配置,与总额限制

        Map<String,Object> result = lotteryRewardRecordService.draw(name, phone);
        if(!(Boolean)result.get("flag")){
            model.addAttribute("error",result.get("error"));
            return  "/lottery/draw_fail";

        }
        model.addAttribute("success",result.get("success"));

        return  "/lottery/draw_success";
    }







    @RequestMapping(value = "/draw")
    @ResponseBody
    public Object draw(String name,String phone ){
        if(AppUtil.isEmpty(name)||AppUtil.isEmpty(phone)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"姓名和联系方式不能为空");
        }
        //当日存在答错题，不允许
        Object object =  lotteryRecordService.checkTodayUser(name,phone,0);
        if(null!=object&&(Long)object>0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"未答对所有题目!");


        }
        //当日已抽过，不允许
        object =  lotteryRewardRecordService.checkTodayUser(name,phone);
        if(null!=object&&(Long)object>0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"今日已参与抽奖");

        }

         //每日抽奖金额配置,与总额限制(TODO
//        LotteryRewardEO lotteryRewardEO = lotteryRewardRecordService.draw(name, phone);

        return getObject();
    }

}

