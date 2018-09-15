package cn.lonsun.content.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "survey", produces = { "application/json;charset=UTF-8" })
public class SurveyController {

	
	@RequestMapping("index")
	public String index() {
		return "/content/survey/theme_list";
	}
}
