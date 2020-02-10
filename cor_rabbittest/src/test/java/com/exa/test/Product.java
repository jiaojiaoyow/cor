package com.exa.test;

import com.exa.rabbittest.RabbitApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitApplication.class)
public class Product {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 直接模式
     */
    @Test
    public void testQueueSend(){
        rabbitTemplate.convertAndSend("test","直接模式测试");
    }

    /**
     * 分列模式
     */
    @Test
    public void testQueueSend1(){
        rabbitTemplate.convertAndSend("ex1","","分裂模式测试");
    }

    /**
     * 主题模式
     * 如果交换器选择错了，例如选择了type为fanout（分列）的，则无法routingkey无法生效
     */
    @Test
    public void testQueueSend2(){
        rabbitTemplate.convertAndSend("ex3","111","主题模式测试");
    }


}
