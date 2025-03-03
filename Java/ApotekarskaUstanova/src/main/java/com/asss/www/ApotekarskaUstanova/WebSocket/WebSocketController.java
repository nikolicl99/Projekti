//package com.asss.www.ApotekarskaUstanova.WebSocket;
//
//import com.asss.www.ApotekarskaUstanova.Entity.Message;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    @Autowired
//    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//    }
//
//    @MessageMapping("/message")
//    public void handleMessage(Message message){
//        System.out.println("Received message from the user: " + message.getUser() + ": " + message.getMessage());
//        simpMessagingTemplate.convertAndSend("/topic/message", message);
//        System.out.println("Sent message to /topic/messages: " + message.getUser() + ": " + message.getMessage());
//    }
//}
