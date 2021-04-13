package com.aifurion.routing;

import com.aifurion.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 10:48
 */
public class Consumer {
    public static void main(String[] args) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            //获取通道对象
            Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare("direct_queue1", false, false, false, null);
            //绑定交换机（routingKey:路由键）
            channel.queueBind("direct_queue1", "direct_exchange", "select");
            channel.queueBind("direct_queue1", "direct_exchange", "insert");
            //监听队列中的消息
            channel.basicConsume("direct_queue1", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    System.out.println("消费者1获得消息为：" + new String(body, StandardCharsets.UTF_8));
                }
            });
            //消费方不需要关闭连接，保持一直监听队列状态
            //connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}