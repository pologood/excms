/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.core.ioc.spring.webapp;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import cn.lonsun.core.util.LsSessionListener;
/**
 * 一个Web监听器，扩展并取代Spring的ContextLoaderListener
 * @author Dzl
 */
public class LonsunContextLoaderListener  extends ContextLoaderListener {

        @Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
//		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
		event.getServletContext().addListener(new LsSessionListener());
	}

}
