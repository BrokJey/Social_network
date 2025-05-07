package org.example.mapper;

import org.example.dto.FriendshipDTO;
import org.example.entity.Friendship;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FriendshipsMapper {
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    FriendshipDTO toDTO(Friendship friendship);

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapToUser")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapToUser")
    Friendship fromDTO(FriendshipDTO dto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
