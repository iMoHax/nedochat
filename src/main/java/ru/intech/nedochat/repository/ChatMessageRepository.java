package ru.intech.nedochat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.intech.nedochat.entity.ChatMessage;

public interface ChatMessageRepository extends PagingAndSortingRepository<ChatMessage, Long> {

}
