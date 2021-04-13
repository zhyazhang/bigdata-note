package com.aifurion.websocket_rabbitmq.controller;

import com.aifurion.websocket_rabbitmq.chat.ChatMessage;
import com.aifurion.websocket_rabbitmq.utils.JsonUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/12 11:04
 */
@Controller
public class ChatController {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 服务端推送给单人的接口
     *
     * @param uid
     * @param content
     */
    @ResponseBody
    @GetMapping("/sendToOne")
    public void sendToOne(@RequestParam("uid") String uid, @RequestParam("content") String content) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setContent(content);
        chatMessage.setTo(uid);
        chatMessage.setSender("系统消息");
        rabbitTemplate.convertAndSend("topicWebSocketExchange", "topic.public", JsonUtil.parseObjToJson(chatMessage));

    }


    /**
     * 接收 客户端传过来的消息 通过setSender和type 来判别时单发还是群发
     *
     * @param chatMessage
     * @param principal
     */
    @MessageMapping("/chat.sendMessageTest")
    public void sendMessageTest(@Payload ChatMessage chatMessage, Principal principal) {
        try {

            //String name = principal.getName();
            chatMessage.setSender("all");
            rabbitTemplate.convertAndSend("topicWebSocketExchange", "topic.public",
                    JsonUtil.parseObjToJson(chatMessage));

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }

    /**
     * 接收 客户端传过来的消息 上线消息
     *
     * @param chatMessage
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {

        System.out.println("有用户加入到了websocket 消息室" + chatMessage.getSender());
        try {

            System.out.println(chatMessage.toString());
            rabbitTemplate.convertAndSend("topicWebSocketExchange", "topic.public",
                    JsonUtil.parseObjToJson(chatMessage));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
