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
import ru.intech.nedochat.entity.Role;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.model.ChatMessageModel;
import ru.intech.nedochat.repository.UserRepository;
import ru.intech.nedochat.service.ChatMessagesService;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {

    private ChatMessagesService chatMessagesService;
    private UserRepository userRepository;

    @Autowired
    public ChatController(ChatMessagesService chatMessagesService, UserRepository userRepository) {
        this.chatMessagesService = chatMessagesService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = {"/chat"})
    public String login(Model model, Principal principal) {
        List<ChatMessage> chatMessages = new ArrayList<>(chatMessagesService.getMessageHistory());
        chatMessages.sort(Comparator.<ChatMessage>naturalOrder());
        model.addAttribute("chatMessages", chatMessages);
        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();
        model.addAttribute("user", user);
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
        if (message.getReceiverId() != null){
            Optional<User> receiver = userRepository.findById(message.getReceiverId());
            receiver.ifPresent(chatMessage::setReceiver);
        }
        chatMessage = chatMessagesService.add(chatMessage);

        return new ChatMessageModel(chatMessage);
    }

    @MessageMapping("/chat.updateMessage")
    @SendTo("/topic/updateMessages")
    @RolesAllowed(Role.ADMIN)
    public ChatMessageModel updateMessage(@Payload ChatMessageModel message) {
        Optional<ChatMessage> chatMessage = chatMessagesService.get(message.getId());
        if (chatMessage.isPresent()){
            ChatMessage cm = chatMessage.get();
            switch (message.getType()){
                case UPDATE:
                        cm.setContent(message.getContent());
                        chatMessagesService.update(cm);
                    break;
                case DELETE:
                        chatMessagesService.delete(cm);
                    break;
            }
            return message;
        } else {
            return new ChatMessageModel();
        }
    }

}
