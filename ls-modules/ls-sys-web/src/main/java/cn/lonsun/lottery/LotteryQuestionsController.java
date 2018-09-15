package cn.lonsun.lottery;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.service.ILotteryQuestionsService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lonsun on 2017-1-11.
 */
@Controller
@RequestMapping(value = "/lotteryQuestions")
public class LotteryQuestionsController extends BaseController {
    @Autowired
    private ILotteryQuestionsService lotteryQuestionsService;




    @RequestMapping(value = "/questionsIndex")
    public String typeIndex() {
        return  "/lottery/questions_index";
    }



    @RequestMapping(value = "/getQuestions")
    @ResponseBody
    public Object getQuestions(QuestionsQueryVO vo){
        Pagination pagination = lotteryQuestionsService.getQuestions(vo);
        return getObject(pagination);
    }



    @RequestMapping(value = "/editQuestions")
    public String editQuestions(Model model,Long lotteryId ) {
        model.addAttribute("lotteryId",lotteryId);
        return  "/lottery/edit_questions";
    }


    @RequestMapping(value = "/getQuestionsById")
    @ResponseBody
    public Object getQuestionsById(Long lotteryId ) {
        LotteryQuestionsEO lotteryQuestionsEO =new LotteryQuestionsEO();
        if(!AppUtil.isEmpty(lotteryId)){

            lotteryQuestionsEO =lotteryQuestionsService.getEntity(LotteryQuestionsEO.class,lotteryId);

        }
        return  getObject(lotteryQuestionsEO);
    }


    @RequestMapping(value = "/saveQuestions")
    @ResponseBody
    public Object saveQuestions(LotteryQuestionsEO  lotteryQuestionsEO ) {
         if(AppUtil.isEmpty(lotteryQuestionsEO.getLotteryId())){
             lotteryQuestionsEO.setSiteId(LoginPersonUtil.getSiteId());
             lotteryQuestionsService.saveEntity(lotteryQuestionsEO);
         }
          else {

             lotteryQuestionsService.updateEntity(lotteryQuestionsEO);
         }

        return getObject(lotteryQuestionsEO);

    }

    @RequestMapping(value = "/delLotteryQuestions")
    @ResponseBody
    public Object  delLotteryQuestions(Long[] ids){
          if(null==ids||ids.length<=0){

              throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待删除项!");

          }

            lotteryQuestionsService.delLotteryQuestions(ids);

         return getObject();
    }
}
