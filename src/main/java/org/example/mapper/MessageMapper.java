package org.example.mapper;

import org.example.dto.MessageDTO;
import org.example.entity.Chat;
import org.example.entity.Message;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "chat", source = "chatId", qualifiedByName = "idToChat")
    @Mapping(target = "sender", source = "senderId", qualifiedByName = "idToUser")
    Message fromDTO(MessageDTO dto);

    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "senderId", source = "sender.id")
    MessageDTO toDTO(Message message);

    @Named("idToChat")
    default Chat idToChat(Long id) {
        if (id == null) return null;
        return Chat.builder().id(id).build();
    }

    @Named("idToUser")
    default User idToUser(Long id) {
        if (id == null) return null;
        return User.builder().id(id).build();
    }
}
