package com.aifurion.websocket_rabbitmq.config;

import com.aifurion.websocket_rabbitmq.websocket.GetHeaderParamInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/12 10:54
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    GetHeaderParamInterceptor getHeaderParamInterceptor;

    @Value("${myrabbitmq.host}")
    private String mq_host;

    @Value("${myrabbitmq.name}")
    private String mq_name;

    @Value("${myrabbitmq.possword}")
    private String mq_possword;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*")
                .withSockJS();
        //暴露多个节点,就一样直接addEndpoint 就可以
//        registry.addEndpoint("/alone") .setAllowedOrigins("*")
//               .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //如果不使用rabbitmq作为消息代理,那么只需要暴露简单节点即可
        // 默认的Simple Broker
        //   registry.enableSimpleBroker("/topic","/user");
        //  registry.setApplicationDestinationPrefixes("/app");


        // 使用RabbitMQ做为消息代理，替换默认的Simple Broker
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀,@SendTo(XXX) 也可以重定向

        registry.setUserDestinationPrefix("/user"); //这是给sendToUser使用,前端订阅需要加上/user

        registry.setApplicationDestinationPrefixes("/app"); //这是给客户端推送消息到服务器使用 ，推送的接口加上/app
        // "STOMP broker relay"处理所有消息将消息发送到外部的消息代理
        registry.enableStompBrokerRelay("/exchange", "/topic", "/queue", "/amq/queue")
                .setVirtualHost("JCChost") //对应自己rabbitmq里的虚拟host
                .setRelayHost(mq_host)
                .setClientLogin(mq_name)
                .setClientPasscode(mq_possword)
                .setSystemLogin(mq_name)
                .setSystemPasscode(mq_possword)
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);

    }

    /**
     * 采用自定义拦截器，获取connect时候传递的参数
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(getHeaderParamInterceptor);
    }
}