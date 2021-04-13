package com.aifurion.publish;

import com.aifurion.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 10:39
 */
public class Consumer {
    public static void main(String[] args) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            //获取通道对象
            Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare("fanout_queue1", false, false, false, null);
            //给队列绑定交换机
            channel.queueBind("fanout_queue1", "fanout_exchange", "");
            //监听队列中的消息
            channel.basicConsume("fanout_queue1", true, new DefaultConsumer(channel) {
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