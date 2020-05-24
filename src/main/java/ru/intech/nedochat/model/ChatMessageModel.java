package ru.intech.nedochat.model;

import ru.intech.nedochat.entity.ChatMessage;
import ru.intech.nedochat.entity.ChatMessageType;

import java.time.LocalDateTime;

public class ChatMessageModel {
    private Long id;
    private ChatMessageType type;
    private String content;
    private Long senderId;
    private String sender;
    private LocalDateTime sendDate;

    public ChatMessageModel() {
    }

    public ChatMessageModel(ChatMessage chatMessage){
        this.id = chatMessage.getId();
        this.type = chatMessage.getType();
        this.content = chatMessage.getContent();
        this.senderId = chatMessage.getSender().getId();
        this.sender = chatMessage.getSender().getName();
        this.sendDate = chatMessage.getSendDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setType(ChatMessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isEventMessage(){
        return type != ChatMessageType.CHAT;
    }
}
