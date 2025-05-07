package org.example.mapper;

import org.example.dto.PostDTO;
import org.example.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostDTO toDTO(Post post);
    Post fromDTO(PostDTO dto);
}
