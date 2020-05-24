package ru.intech.nedochat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.intech.nedochat.entity.ChatMessage;
import ru.intech.nedochat.entity.ChatMessageType;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.model.ChatMessageModel;
import ru.intech.nedochat.service.ChatMessagesService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChatController {

    private ChatMessagesService chatMessagesService;

    @Autowired
    public ChatController(ChatMessagesService chatMessagesService) {
        this.chatMessagesService = chatMessagesService;
    }

    @GetMapping(value = {"/chat"})
    public String login(Model model) {
        List<ChatMessage> chatMessages = new ArrayList<>(chatMessagesService.getMessageHistory());
        chatMessages.sort(Comparator.<ChatMessage>naturalOrder());
        model.addAttribute("chatMessages", chatMessages);
        return "chat";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public ChatMessageModel sendMessage(@Payload ChatMessageModel message, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.CHAT);
        chatMessage.setSender(user);
        chatMessage.setSendDate(LocalDateTime.now());
        chatMessage.setContent(message.getContent());
        chatMessage = chatMessagesService.add(chatMessage);

        return new ChatMessageModel(chatMessage);
    }

}
