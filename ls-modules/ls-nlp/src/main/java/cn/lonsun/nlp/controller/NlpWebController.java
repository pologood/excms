package cn.lonsun.nlp.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.nlp.internal.entity.NlpMemberLabelRelEO;
import cn.lonsun.nlp.internal.service.INlpKeyWordsArticleRelService;
import cn.lonsun.nlp.internal.service.INlpKeyWordsMemberRelService;
import cn.lonsun.nlp.internal.service.INlpKeyWordsService;
import cn.lonsun.nlp.internal.service.INlpMemberLabelRelService;
import cn.lonsun.nlp.internal.vo.ContentVO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import cn.lonsun.nlp.utils.DateUtil;
import cn.lonsun.system.member.vo.MemberSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员标签
 * @Author: liuk
 * @Date: 2018-6-29 17:09:44
 */
@Controller
@RequestMapping(value = "nlp")
public class NlpWebController extends BaseController {


    @Autowired
    private INlpKeyWordsArticleRelService nlpKeyWordsArticleRelService;
    @Autowired
    private INlpKeyWordsMemberRelService nlpKeyWordsMemberRelService;
    @Autowired
    private INlpKeyWordsService nlpKeyWordsService;
    @Autowired
    private INlpMemberLabelRelService nlpMemberLabelRelService;


    /**
     * 查询会员关注的标签列表
     * @param memberId
     * @return
     */
    @RequestMapping("getMemberLabel")
    @ResponseBody
    public Object getMemberLabel(Long memberId){
        if(memberId==null){
            MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
            if(memberVO==null){
                return ajaxErr("请先登录");
            }
            memberId = memberVO.getId();
        }
        return getObject(nlpMemberLabelRelService.getLabelByMemberId(memberId));
    }

    /**
     * 增加关注标签
     * @param memberId
     * @return
     */
    @RequestMapping("addMemberLabel")
    @ResponseBody
    public Object addMemberLabel(Long memberId,Long labelId,Long siteId){
        if(memberId==null){
            MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
            if(memberVO==null){
                return ajaxErr("请先登录");
            }
            memberId = memberVO.getId();
            if(siteId==null){
                siteId = memberVO.getSiteId();
            }
        }

        if(labelId==null){
            return ajaxErr("请选择你想要关注的标签");
        }

        if(siteId==null){
            return ajaxErr("siteId不能为空");
        }

        nlpMemberLabelRelService.saveMemberLabel(memberId,labelId,siteId);

        return getObject();
    }

    /**
     * 删除关注标签
     * @param memberId
     * @return
     */
    @RequestMapping("delMemberLabel")
    @ResponseBody
    public Object delMemberLabel(Long memberId,Long labelId){
        if(memberId==null){
            MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
            if(memberVO==null){
                return ajaxErr("请先登录");
            }
            memberId = memberVO.getId();
        }

        if(labelId==null){
            return ajaxErr("请选择你想要关注的标签");
        }

        nlpMemberLabelRelService.delMemberLabel(memberId,labelId);

        return getObject();
    }


    /**
     * 查询“猜你关注”列表
     * @param memberId
     * @return
     */
    @RequestMapping("getKeyWordList")
    @ResponseBody
    public Object getKeyWordList(Long memberId,Long siteId,Integer num){
        if(memberId==null){
            MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
            if(memberVO==null){
                return ajaxErr("请先登录");
            }
            memberId = memberVO.getId();
            if(siteId==null){
                siteId = memberVO.getSiteId();
            }
        }
        return getObject(nlpKeyWordsMemberRelService.getMemberKeywords(memberId,siteId,num));
    }

    /**
     * 查询单个关键词关联的内容列表
     * @param labelId
     * @param siteId
     * @param type today-今天、threeDays近三天、week最近一周
     * @param num
     * @return
     */
    @RequestMapping("getNewsByLabelId")
    @ResponseBody
    public Object getNewsByLabelId(@RequestParam("labelId") Long[] labelId, Long siteId, String type, Integer num){
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if(memberVO==null){
            return ajaxErr("请先登录");
        }
        if(siteId==null){
            siteId = memberVO.getSiteId();
        }
        if(labelId==null||labelId.length==0){
            List<NlpKeyWordsVO> labelList = nlpMemberLabelRelService.getLabelByMemberId(memberVO.getId());
            List<Long> labelIdList = new ArrayList<Long>();
            if(labelList!=null&&labelList.size()>0){//查询会员关注的标签，从而推荐相关信息
                for(NlpKeyWordsVO vo:labelList){
                    labelIdList.add(vo.getKeyWordId());
                }
                labelId = labelIdList.toArray(new Long[]{});
            }else{//会员尚未关注标签，则根据会员浏览记录推荐信息

                //获取会员访问次数最多的20个关键词，从而推荐相关信息
                labelList = nlpKeyWordsMemberRelService.getMemberKeywords(memberVO.getId(),memberVO.getSiteId(),20);
                if(labelList==null||labelList.size()==0){
                    //会员没有文章页浏览记录，则从整个网站中筛选出20个访问量最高的关键词
                    labelList = nlpKeyWordsMemberRelService.getMemberKeywords(null,memberVO.getSiteId(),20);
                }
                if(labelList!=null&&labelList.size()>0){
                    for(int i=0;i<labelList.size();i++){
                        labelIdList.add(labelList.get(i).getKeyWordId());
                    }
                    labelId = labelIdList.toArray(new Long[]{});
                }
            }
        }
        Date st = getStartDate(type);//获取开始时间
        Date ed = null;
        List<ContentVO> contentList = nlpKeyWordsArticleRelService.queryContentsByKeyWordId(labelId,siteId,st,ed,num);
        return getObject(contentList);
    }


    /**
     * 查询单个关键词关联的内容分页
     * @param labelId
     * @param siteId
     * @param type today-今天、threeDays近三天、week最近一周
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @RequestMapping("getNewsPage")
    @ResponseBody
    public Object getNewsPage(@RequestParam("labelId") Long[] labelId, Long siteId, String type,
                              Long pageIndex,Integer pageSize){
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if(memberVO==null){
            return ajaxErr("请先登录");
        }
        if(siteId==null){
            siteId = memberVO.getSiteId();
        }
        if(labelId==null||labelId.length==0){
            List<NlpKeyWordsVO> labelList = nlpMemberLabelRelService.getLabelByMemberId(memberVO.getId());
            List<Long> labelIdList = new ArrayList<Long>();
            if(labelList!=null&&labelList.size()>0){//查询会员关注的标签，从而推荐相关信息
                for(NlpKeyWordsVO vo:labelList){
                    labelIdList.add(vo.getKeyWordId());
                }
                labelId = labelIdList.toArray(new Long[]{});
            }else{//会员尚未关注标签，则根据会员浏览记录推荐信息

                //获取会员访问次数最多的20个关键词，从而推荐相关信息
                labelList = nlpKeyWordsMemberRelService.getMemberKeywords(memberVO.getId(),memberVO.getSiteId(),20);
                if(labelList==null||labelList.size()==0){
                    //会员没有文章页浏览记录，则从整个网站中筛选出20个访问量最高的关键词
                    labelList = nlpKeyWordsMemberRelService.getMemberKeywords(null,memberVO.getSiteId(),20);
                }
                if(labelList!=null&&labelList.size()>0){
                    for(int i=0;i<labelList.size();i++){
                        labelIdList.add(labelList.get(i).getKeyWordId());
                    }
                    labelId = labelIdList.toArray(new Long[]{});
                }
            }
        }

        if(labelId==null||labelId.length==0){
            return getObject();
        }

        if(pageIndex==null){
            pageIndex = 0l;
        }
        if(pageSize==null){
            pageSize = 15;
        }
        Date st = getStartDate(type);//获取开始时间
        Date ed = null;

        return getObject(nlpKeyWordsArticleRelService.queryContentPage(labelId, siteId, st, ed, pageIndex, pageSize));
    }

    /**
     * 根据查询类型获取开始时间
     * @param type
     * @return
     */
    private Date getStartDate(String type){
        if(!AppUtil.isEmpty(type)){
            if(DateUtil.TODAY.equals(type)){
                return DateUtil.getToday();
            }else if(DateUtil.THREEDAYS.equals(type)){
                return DateUtil.getAnyday(-2);//获取近三天的开始时间
            }else if(DateUtil.WEEK.equals(type)){
                return DateUtil.getAnyday(-6);//获取近一周的开始时间
            }
        }
        return null;
    }

}
