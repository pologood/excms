package cn.lonsun.publicInfo.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.publicInfo.internal.entity.PublicReportEO;
import cn.lonsun.publicInfo.vo.PublicReportVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by fth on 2017/1/19.
 */
public interface IPublicReportService extends IMockService<PublicReportEO> {

    List<PublicReportEO> getPublicReportList();

    List<PublicReportEO> getPublicReportTjList(PublicReportVO publicReportVO);

    void exportPublicReportList(PublicReportVO publicReportVO, HttpServletResponse response);
}
