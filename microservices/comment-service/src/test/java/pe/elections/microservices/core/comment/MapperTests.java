package pe.elections.microservices.core.comment;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.core.comment.persistence.CommentEntity;
import pe.elections.microservices.core.comment.services.CommentMapper;

class MapperTests {
    private CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void mapperTests() {
        assertNotNull(mapper);
        Comment api = new Comment(1, 2, "Content comment", "Author comment", LocalDateTime.now(), "adr");
        CommentEntity entity = mapper.apiToEntity(api);
        assertEquals(api.getCandidateId(), entity.getCandidateId());
        assertEquals(api.getCommentId(), entity.getCommentId());
        assertEquals(api.getContent(), entity.getContent());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getCreatedAt(), entity.getCreatedAt());
        Comment api2 = mapper.entityToApi(entity);
        assertEquals(api.getCandidateId(), api2.getCandidateId());
        assertEquals(api.getCommentId(), api2.getCommentId());
        assertEquals(api.getContent(), api2.getContent());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getCreatedAt(), api2.getCreatedAt());
        assertNull(api2.getServiceAddress());
    }
}
