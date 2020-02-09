package com.exa.rabbittest.customer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "test2")
public class Customer2 {
    @RabbitHandler
    public void getMsg(String msg){
        System.out.println("分例模式1+test2:"+msg);
    }
}
