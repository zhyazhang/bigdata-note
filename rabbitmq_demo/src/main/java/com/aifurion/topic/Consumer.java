package com.aifurion.topic;

import com.aifurion.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 10:55
 */
public class Consumer {
    public static void main(String[] args) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            //获取通道对象
            Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare("topic_queue1", false, false, false, null);
            //绑定交换机（routingKey:路由键）  #：匹配0-n个单词（之间以.区分，两点之间算一个单词）
            channel.queueBind("topic_queue1", "topic_exchange", "emp.#");
            //监听队列中的消息
            channel.basicConsume("topic_queue1", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    System.out.println("消费者1获得消息为：" + new String(body, "utf-8"));
                }
            });
            //消费方不需要关闭连接，保持一直监听队列状态
            //connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}