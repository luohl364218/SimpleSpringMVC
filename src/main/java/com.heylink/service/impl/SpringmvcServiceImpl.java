package com.heylink.service.impl;

import com.heylink.annotation.Service;
import com.heylink.service.SpringmvcService;

import java.util.Map;

/*
 * introductions:
 * created by Heylink on 2018/4/23 17:06
 */
@Service("SpringmvcServiceImpl")
public class SpringmvcServiceImpl implements SpringmvcService{

    @Override
    public int insert(Map map) {
        System.out.println("SpringmvcServiceImpl:" + "insert");
        return 0;
    }

    @Override
    public int delete(Map map) {
        System.out.println("SpringmvcServiceImpl:" + "delete");
        return 0;
    }

    @Override
    public int update(Map map) {
        System.out.println("SpringmvcServiceImpl:" + "update");
        return 0;
    }

    @Override
    public int select(Map map) {
        System.out.println("SpringmvcServiceImpl:" + "select");
        return 0;
    }
}
