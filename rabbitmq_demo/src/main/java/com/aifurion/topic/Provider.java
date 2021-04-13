package com.aifurion.topic;

import com.aifurion.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 10:54
 */
public class Provider {
    public static void main(String[] args) {
        try {
            //获取连接对象
            Connection connection = ConnectionUtil.getConnection();
            //获取通道对象
            Channel channel = connection.createChannel();
            //创建交换机（交换机没有存储数据的能力，数据存储在队列上，如果有交换机没队列的情况下，数据会丢失）   //1.参数一：交换机名称    参数二：交换机类型
            channel.exchangeDeclare("topic_exchange", "topic");
            //向队列中发送消息
            for (int i = 1; i <= 10; i++) {
                channel.basicPublish("topic_exchange",
                        "emp.hello world",  // #:匹配0-n个单词(之间以.区分,两点之间算一个单词，可以匹配hello world空格的情况)   *(匹配一个单词)
                        null,
                        ("Hello RabbitMQ!!!" + i).getBytes());
            }
            //断开连接
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}