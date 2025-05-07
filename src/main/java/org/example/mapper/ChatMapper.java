package org.example.mapper;

import org.example.dto.ChatDTO;
import org.example.entity.Chat;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "participantIds", source = "participants", qualifiedByName = "mapUsersToIds")
    ChatDTO toDTO(Chat chat);

    @Mapping(target = "participants", source = "participantIds", qualifiedByName = "mapIdsToUsers")
    Chat fromDTO(ChatDTO dto);

    @Named("mapUsersToIds")
    default Set<Long> mapUsersToIds(Set<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapIdsToUsers")
    default Set<User> mapIdsToUsers(Set<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> User.builder().id(id).build())
                .collect(Collectors.toSet());
    }
}
