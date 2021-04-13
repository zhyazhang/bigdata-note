package com.aifurion.work;

import com.aifurion.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 10:15
 */
public class Provider {
    public static void main(String[] args) {
        try {
            //获取连接对象
            Connection connection = ConnectionUtil.getConnection();
            //获取通道对象
            Channel channel = connection.createChannel();
            //通过通道创建队列
            channel.queueDeclare("queue1", false, false, false, null);
            //向队列中发送消息
            for (int i = 1; i <= 10; i++) {
                channel.basicPublish("", "queue1", null, ("Hello RabbitMQ!!!" + i).getBytes());
            }
            //断开连接
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
