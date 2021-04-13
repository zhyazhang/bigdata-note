package com.aifurion.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/13 9:38
 */
public class ConnectionUtil {
    //连接rabbitmq服务，共享一个工厂对象
    private static ConnectionFactory factory;

    static {
        factory = new ConnectionFactory();
        //设置rabbitmq属性
        factory.setHost("192.168.236.211");
        factory.setUsername("zzy");
        factory.setPassword("123");
        factory.setVirtualHost("JCChost");
        factory.setPort(5672);
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            //获取连接对象
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
