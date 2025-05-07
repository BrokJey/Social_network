package org.example.mapper;

import org.example.dto.MessageDTO;
import org.example.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageDTO toDTO(Message message);
    Message fromDTO(MessageDTO dto);
}
