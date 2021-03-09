//package pers.cyz.bigdatatool.uiservice.controller;
//
//
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//
//
//@ServerEndpoint("/websocket/download")
//@Component
//public class WebSocketServer {
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("open");
//
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose() {
//        System.out.println("close");
//
//    }
//
//    /**
//     * 收到客户端消息后调用的方法
//     *
//     * @param message 客户端发送过来的消息*/
//    @OnMessage
//    public void onMessage(String message, Session session) {
//
//        System.out.println("收到"+  message);
//
//    }
//
//
//    @OnError
//    public void onError(Session session, Throwable error) {
//        System.out.println("ere");
//
//    }
//    /**
//     * 实现服务器主动推送
//     */
//}