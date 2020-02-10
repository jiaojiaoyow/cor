package com.exa.rabbittest.customer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
//指定消费的队列
@RabbitListener(queues = "test")
public class Customer1 {

    @RabbitHandler
    public void getMsg(String msg){
        System.out.println("直接模式1:"+msg);
    }

}
