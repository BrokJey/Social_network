package org.example.mapper;

import org.example.dto.PostDTO;
import org.example.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "createdAt", target = "createAt")
    PostDTO toDTO(Post post);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "community", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Post fromDTO(PostDTO dto);
}
