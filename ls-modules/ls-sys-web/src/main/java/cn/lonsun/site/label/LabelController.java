package cn.lonsun.site.label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.label.internal.service.ILabelService;
import cn.lonsun.site.label.internal.vo.LabelVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author DooCal
 * @ClassName: LabelController
 * @Description:
 * @date 2015/9/6 17:56
 */
@Controller
@RequestMapping(value = "label")
public class LabelController extends BaseController {

  @Autowired
  private ILabelService labelService;

  @RequestMapping(value = "index")
  public String label_index(Model model) {
    model.addAttribute("isRoot", LoginPersonUtil.isRoot());
    return "/site/label/label_index";
  }

  @RequestMapping(value = "add")
  public String label_add(@RequestParam(defaultValue = "0") Long pid, Model model) {
    model.addAttribute("id", 0);
    model.addAttribute("parentId", pid);
    return "/site/label/label_edit";
  }

  @RequestMapping(value = "list")
  public String label_list(@RequestParam(defaultValue = "0") Long id, HttpServletRequest request, Model model) {

    if (id == 0) {
    } else {
      LabelEO labelEO = labelService.getById(id);
      model.addAttribute("id", id);
      model.addAttribute("labelConfig", AppUtil.isEmpty(labelEO.getLabelConfig()) ? "[]" : labelEO.getLabelConfig());
    }
    model.addAttribute("isRoot", LoginPersonUtil.isRoot());

    return "/site/label/label_list";
  }

  @RequestMapping(value = "edit")
  public String label_edit(@RequestParam(defaultValue = "0") Long id, @RequestParam(defaultValue = "0") Long pid, HttpServletRequest request, Model model) {

    model.addAttribute("id", id);
    model.addAttribute("pid", pid);

    String labelName = "", labelNotes = "";
    if (id > 0) {
      LabelEO labelEO = labelService.getById(id);
      labelName = labelEO.getLabelName();
      labelNotes = labelEO.getLabelNotes();
    }
    System.out.println(labelNotes);
    model.addAttribute("labelName", labelName);
    model.addAttribute("labelNotes", labelNotes);

    return "/site/label/label_edit";
  }

  @RequestMapping(value = "config")
  public String label_config(@RequestParam(defaultValue = "0") Long uid, Model model) {

    model.addAttribute("uid", uid);
    return "/site/label/label_config";

  }

  @RequestMapping(value = "tree")
  @ResponseBody
  public Object label_tree(@RequestParam(defaultValue = "0") Long pid, HttpServletRequest request, String searchName) {
    List<LabelVO> list = null;
    if (AppUtil.isEmpty(searchName)) {
      list = labelService.getTree(pid);
    } else {
      list = labelService.getByName(searchName);
    }
    Map<String, Object> map = new HashMap<String, Object>();
    String dataFlag = request.getParameter("dataFlag");
    if (!AppUtil.isEmpty(dataFlag) && request.getParameter("dataFlag").equals("1")) {
      return getObject(list);
    }
    map.put("list", list);
    return getObject(map);
  }

  @RequestMapping(value = "saveLabel")
  @ResponseBody
  public Object saveLabel(LabelEO eo) {
    return getObject(labelService.saveLabel(eo));
  }

  @RequestMapping(value = "editLabel")
  @ResponseBody
  public Object updateLabel(LabelEO eo) {
    return getObject(labelService.updateLabel(eo));
  }

  @RequestMapping(value = "delLabel")
  @ResponseBody
  public Object delLabel(Long id) {
    return getObject(labelService.delLabel(id));
  }

  @RequestMapping(value = "editLabelConfig")
  @ResponseBody
  public Object editLabelConfig(Long id, String labelConfig) {
    return getObject(labelService.updateLabelConfig(id, labelConfig));
  }

  @RequestMapping(value = "copyLabel")
  @ResponseBody
  public Object copyLabel(Long id, String labelName, String labelNotes) {
    return getObject(labelService.copyLabel(id, labelName, labelNotes));
  }

}
