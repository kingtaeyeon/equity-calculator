package com.ec.framework.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName App
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 23:46
 */
@SpringBootApplication
@ComponentScan("com.ec.framework")
public class App {

    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }
}
