package cn.lonsun.staticcenter.controller;

import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.interview.internal.service.IInterviewQuestionService;
import cn.lonsun.content.interview.vo.InterviewStatusVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookStatusVO;
import cn.lonsun.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公众参与度分析
 * Created by zx on 2016-6-17.
 */

@Controller
@RequestMapping(value = "/analyse", produces = {"application/json;charset=UTF-8"})
public class ParticipationAnalyseController extends BaseController {

    @Autowired
    private IInterviewQuestionService interviewQuestionService;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IMessageBoardService messageBoardService;
    /**
     * 在线访谈参与度统计
     *
     * @param vo
     */
    @RequestMapping("/getInterview")
    @ResponseBody
    public Object getInterview(InterviewStatusVO vo) {
        if (vo.getId() == null) {
            return ajaxParamsErr("id不能为空！");
        }

        return getObject(interviewQuestionService.getParticipationAnalyseById(vo));
    }

    /**
     * 留言管理参与度统计\ 办件统计
     *
     * @param vo
     */
    @RequestMapping("/getGuestBook")
    @ResponseBody
    public Object getGuestBook(GuestBookStatusVO vo) {
        if (vo.getColumnIds() == null) {
            return ajaxParamsErr("请输入栏目id！");
        }

        if (vo.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }

        return getObject(guestBookService.getAnalys(vo));
    }

    /**
     * 多回复留言管理参与度统计\ 办件统计
     *
     * @param vo
     */
    @RequestMapping("/getMessageBoard")
    @ResponseBody
    public Object getMessageBoard(GuestBookStatusVO vo) {
        if (vo.getColumnIds() == null) {
            return ajaxParamsErr("请输入栏目id！");
        }

        if (vo.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }

        return getObject(messageBoardService.getAnalys(vo));
    }

}
