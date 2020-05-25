package ru.intech.nedochat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.intech.nedochat.entity.ChatMessage;
import ru.intech.nedochat.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ChatMessagesService {

    private ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessagesService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }


    public ChatMessage add(ChatMessage message){
        if (message.getSendDate() == null){
            message.setSendDate(LocalDateTime.now());
        }
        return chatMessageRepository.save(message);
    }

    public ChatMessage update(ChatMessage message){
        return chatMessageRepository.save(message);
    }

    public void delete(ChatMessage message){
        chatMessageRepository.delete(message);
    }

    public Optional<ChatMessage> get(Long id){
        return chatMessageRepository.findById(id);
    }

    public List<ChatMessage> getMessageHistory(){
        PageRequest page = PageRequest.of(0, 12, Sort.by("sendDate").descending());
        return chatMessageRepository.findAll(page).getContent();
    }
}
