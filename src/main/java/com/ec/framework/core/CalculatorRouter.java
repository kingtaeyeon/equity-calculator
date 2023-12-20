package com.ec.framework.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根据注解路由到目标Calculator
 *
 * @ClassName CalculatorRouter
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 13:30
 */
@Component
public class CalculatorRouter implements ApplicationContextAware {


    private Map<String, Calculator> map;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        map = applicationContext.getBeansOfType(Calculator.class);
    }

    public Map<String, Calculator> getMap() {
        return map;
    }

    public Calculator getService(String key) {
        return map.get(key);
    }
}
