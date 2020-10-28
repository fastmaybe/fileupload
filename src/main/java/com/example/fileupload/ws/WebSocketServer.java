package com.example.fileupload.ws;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: liulang
 * @Date: 2020/10/28 17:27
 */
@ServerEndpoint("/ws/progress")
@Component
public class WebSocketServer {



    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketTestController对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        WebSocketServer.webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        //群发消息
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message)  {
        try {
            this.session.getBasicRemote().sendText(message);
        }catch (IOException e) {
            System.out.println("推送消息异常");
        }
        //this.session.getAsyncRemote().sendText(message);
    }

    public void sendObject(Object object) {
        try{
            this.session.getBasicRemote().sendObject(object);
        }catch (EncodeException | IOException e) {
            System.out.println("推送消息异常");
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) {
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage(message);
        }
    }
    /**
     * 群发自定义对象
     */
    public static void sendInfo(Object message) {
        for (WebSocketServer item : webSocketSet) {
            item.sendObject(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
