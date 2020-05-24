package ru.intech.nedochat.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.intech.nedochat.entity.ChatMessageType;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.model.ChatMessageModel;

import java.time.LocalDateTime;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User user = (User) ((UsernamePasswordAuthenticationToken)headerAccessor.getUser()).getPrincipal();

        if (user != null) {
            logger.info("User {} connected to chat", user.getUsername());

            ChatMessageModel chatMessage = new ChatMessageModel();
            chatMessage.setType(ChatMessageType.JOIN);
            chatMessage.setSendDate(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User user = (User) ((UsernamePasswordAuthenticationToken)headerAccessor.getUser()).getPrincipal();

        if (user != null) {
            logger.info("User {} disconnected", user.getUsername());

            ChatMessageModel chatMessage = new ChatMessageModel();
            chatMessage.setType(ChatMessageType.LEAVE);
            chatMessage.setSendDate(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }
}
