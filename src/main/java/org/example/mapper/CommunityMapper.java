package org.example.mapper;

import org.example.dto.CommunityDTO;
import org.example.entity.Community;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "adminId", source = "admin.id")
    CommunityDTO toDTO(Community community);

    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapToUser")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "posts", ignore = true)
    Community fromDTO(CommunityDTO dto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        if (id == null) return null;
        return User.builder().id(id).build();
    }
}
