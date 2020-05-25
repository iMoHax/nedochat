package ru.intech.nedochat.model;

import ru.intech.nedochat.entity.ChatMessage;
import ru.intech.nedochat.entity.ChatMessageType;

import java.time.LocalDateTime;

public class ChatMessageModel {
    private Long id;
    private ChatMessageType type;
    private String content;
    private Integer senderId;
    private String sender;
    private Integer receiverId;
    private String receiver;
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

        if (chatMessage.getReceiver() != null){
            this.receiverId = chatMessage.getReceiver().getId();
            this.receiver = chatMessage.getReceiver().getName();
        }
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

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
