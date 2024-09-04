package com.qin.catcat.unite.controller.handle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.service.task.CatLocationTask;

import lombok.extern.slf4j.Slf4j;

/* 轨迹更新服务WebSocket处理器 
 * 访问地址：ws://localhost:8080/catLocation
*/
@Component
@Slf4j
public class CatWebSocketHandler extends TextWebSocketHandler{
    // 注入 CatLocationTask，用于检查并启动或停止定时任务
    private final CatLocationTask catLocationTask;

    // 订阅列表，用于存储已订阅 轨迹更新服务 的会话
    private final Set<WebSocketSession> subscribedSessions = new HashSet<>();

    // 构造器注入
    public CatWebSocketHandler(@Lazy CatLocationTask catLocationTask){
        this.catLocationTask = catLocationTask;
    }

    // 处理订阅消息
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session,@NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 解析消息，如果是订阅了轨迹更新消息，将会话加入订阅列表
        
        if (payload.contains("\"action\": \"subscribe\"") && payload.contains("\"type\": \"track\"")){
            subscribedSessions.add(session);
            log.info("Session {} subscribed to track updates", session.getId());
            // 检查并启动定时任务，每5秒执行一次
            if (catLocationTask != null) {// 确保 CatLocationTask 已正确注入
                catLocationTask.checkAndStartTask();
            } else {
                log.error("CatLocationTask is not injected properly.");
            }
        }

        
    }

    // 获取订阅列表
    public Set<WebSocketSession> getSubscribedSessions(){
        return subscribedSessions;
    }

    // 给某一个session会话发送消息：轨迹更新数据
    public void sendLocaltionUpdate(WebSocketSession session, List<CoordinateVO> locationData) throws Exception {
        // // 将 List<CoordinateVO> 转换为 JSON 字符串
        String message = JSON.toJSONString(locationData);
        session.sendMessage(new TextMessage(message));
    }

    // 处理会话关闭或退出
    // 为了防止订阅列表中保留无效的会话，当用户关闭页面或连接断开时，应将该用户的会话从订阅列表中移除
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull CloseStatus status) throws Exception{
        subscribedSessions.remove(session);
        super.afterConnectionClosed(session,status);
    }

}
