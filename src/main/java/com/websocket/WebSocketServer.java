package com.websocket;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@ServerEndpoint(value = "/websockets/logger")
public class WebSocketServer {

    private Logger logger = Logger.getLogger(getClass());
    private Logger loginLogger = Logger.getLogger("login");
    private Logger goodsLogger = Logger.getLogger("goods");
    private Logger orderLogger = Logger.getLogger("order");
    private Logger manageLogger = Logger.getLogger("manage");
    private Logger couponLogger = Logger.getLogger("coupon");
    
    private Logger _logger = null;
    
    private static final Map<String,Logger> logs = new HashMap<String,Logger>();
    {
    	logs.put("login", loginLogger);
    	logs.put("goods", goodsLogger);
    	logs.put("order", orderLogger);
    	logs.put("manage", manageLogger);
    	logs.put("coupon", couponLogger);
    }
    
    private static final Set<WebSocketServer> connections = new CopyOnWriteArraySet<WebSocketServer>();
    
    private Session session;
    
    @OnOpen
    public void onOpen(Session session) {
    	this.session = session;
    	connections.add(this);
        logger.info("Connected ... " + session.getId());
    }

    @OnMessage
	public void onMessage(String unscrambledWord, Session session) {
    	JSONObject json = JSON.parseObject(unscrambledWord);
    	//日志等级
    	String grade = json.getString("grade");
    	//日志内容
    	String message = json.getString("message");
    	//设备机型
    	String type = json.getString("type");
    	//日记分组
    	String name = json.getString("name");
    	
    	Iterator<Map.Entry<String, Logger>> iterator = logs.entrySet().iterator();
    	
    	while(iterator.hasNext()){
    		Map.Entry<String, Logger> i = iterator.next();
    		String log = i.getKey();
    		Logger logger = i.getValue();
    		
    		if(log.equals(name)){
        		_logger = logger;
        		break;
    		}
    	}
    	
    	if(null == _logger){
    		_logger = logger;
    	}
    	
    	StringBuffer info = new StringBuffer();
    	info.append("设备机型:").append(type).append(",Session:").append(session.getId()).append("-->").append(message);
    	
    	if("info".equals(grade)){
    		_logger.info(info);
    	}else if("error".equals(grade)){
    		_logger.error(info);
    	}else if("warn".equals(grade)){
    		_logger.warn(info);
    	}else if("log".equals(grade)){
    		_logger.info(info);
    	}else{
    		grade = "log";
    		_logger.info(info);
    	}
    	
    	JSONObject reslut = new JSONObject();
    	reslut.put("grade", grade);
    	reslut.put("message", info);
    	
    	for(WebSocketServer webSocketServer : connections){
    		try {
				synchronized (webSocketServer) {
					if(webSocketServer.session.isOpen()){
						webSocketServer.session.getBasicRemote().sendText(reslut.toJSONString());
					}
				}
			} catch (Exception e) {
				logger.error("发送日志失败:" + e.getMessage(),e);
			}
    	}
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	connections.remove(this);
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}
