package com.aifurion.websocket_rabbitmq.chat;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/4/12 11:03
 */
@Service
public class ChatService {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    public Boolean sendMsg(String msg) {

        try {
            JSONObject msgJson = JSONObject.parseObject(msg);
            if (msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.CHAT.toString())) {
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            } else if (msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.JOIN.toString())) {
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            } else if (msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.LEAVE.toString())) {
                simpMessageSendingOperations.convertAndSend("/topic/public", msgJson);

            } else if (!msgJson.getString("to").equals("all") && msgJson.getString("type").equals(ChatMessage.MessageType.CHAT.toString())) {

                simpMessageSendingOperations.convertAndSendToUser(msgJson.getString("to"), "/topic/msg", msgJson);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


}
