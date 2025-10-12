package org.example.mapper;

import org.example.dto.CommentDTO;
import org.example.entity.Comment;
import org.example.entity.Post;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "postId", source = "post.id")
    CommentDTO toDTO(Comment comment);

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapToUser")
    @Mapping(target = "post", source = "postId", qualifiedByName = "mapToPost")
    Comment fromDTO(CommentDTO dto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        if (id == null) return null;
        return User.builder().id(id).build();
    }

    @Named("mapToPost")
    default Post mapToPost(Long id) {
        if (id == null) return null;
        return Post.builder().id(id).build();
    }
}
