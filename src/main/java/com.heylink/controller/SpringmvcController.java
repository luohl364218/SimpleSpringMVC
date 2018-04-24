package com.heylink.controller;

import com.heylink.annotation.Controller;
import com.heylink.annotation.Qualifier;
import com.heylink.annotation.RequestMapping;
import com.heylink.service.MyService;
import com.heylink.service.SpringmvcService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * introductions:
 * created by Heylink on 2018/4/23 17:24
 */
@Controller("test")
public class SpringmvcController {

    @Qualifier("MyServiceImpl")
    MyService myService;
    @Qualifier("SpringmvcServiceImpl")
    SpringmvcService springmvcService;

    @RequestMapping("insert")
    public String insert(HttpServletRequest request, HttpServletResponse response, String param) {
        myService.insert(null);
        springmvcService.insert(null);
        return null;
    }

    @RequestMapping("delete")
    public String delete(HttpServletRequest request, HttpServletResponse response, String param) {
        myService.delete(null);
        springmvcService.delete(null);
        return null;
    }

    @RequestMapping("update")
    public String update(HttpServletRequest request, HttpServletResponse response, String param) {
        myService.update(null);
        springmvcService.update(null);
        return null;
    }

    @RequestMapping("select")
    public String select(HttpServletRequest request, HttpServletResponse response, String param) {
        myService.select(null);
        springmvcService.select(null);
        return null;
    }
}
