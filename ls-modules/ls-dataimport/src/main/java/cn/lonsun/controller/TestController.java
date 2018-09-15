package cn.lonsun.controller;


import cn.lonsun.internal.metadata.ImportType;
import cn.lonsun.source.dataexport.vo.PublicUnitQueryVO;
import cn.lonsun.target.dataimport.service.publicinfo.IPublicUnitImportService;
import cn.lonsun.target.datamodel.publicinfo.PublicUnitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("importTest")
public class TestController {

    private Long siteId = 4133646L;

    @Autowired
    private IPublicUnitImportService publicUnitImportService;

    @ResponseBody
    @RequestMapping("testPublicUnit")
    public void testPublicUnit(){
        List<PublicUnitVO> data = new ArrayList<PublicUnitVO>();
        data.add(new PublicUnitVO(6931772L,11111111L, 1L, "oldId1", "oldName1"));
//        data.add(new PublicUnitVO(11111112L,11111112L, 2L, "oldId2", "oldName2"));
        PublicUnitQueryVO queryVO = new PublicUnitQueryVO();
        queryVO.setImportType(ImportType.appoint);
        queryVO.setSiteId(siteId);
        queryVO.setOldContentIds(new String[]{"oldId1","oldId2"});
        publicUnitImportService.importData(data, queryVO);

    }


}
