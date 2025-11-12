package pe.elections.microservices.core.comment;


import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

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
        Instant instant = LocalDateTime.of(2024, 1, 15, 14, 30, 0).atZone(ZoneId.of("UTC")).toInstant();
        Comment api = new Comment(1, 2, "Content comment", "Author comment", instant, "adr");
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

    @Test
    void mapperListTests() {
        assertNotNull(mapper);
        Instant instant = LocalDateTime.of(2024, 1, 15, 14, 30, 0).atZone(ZoneId.of("UTC")).toInstant();
        Comment api = new Comment(1, 2, "Content comment", "Author comment", instant, "adr");
        List<Comment> apiList = Collections.singletonList(api);
        List<CommentEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());
        CommentEntity entity = entityList.get(0);
        assertEquals(api.getCandidateId(), entity.getCandidateId());
        assertEquals(api.getCommentId(), entity.getCommentId());
        assertEquals(api.getContent(), entity.getContent());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getCreatedAt(), entity.getCreatedAt());
        List<Comment> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());
        Comment api2 = api2List.get(0);
        assertEquals(api.getCandidateId(), api2.getCandidateId());
        assertEquals(api.getCommentId(), api2.getCommentId());
        assertEquals(api.getContent(), api2.getContent());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getCreatedAt(), api2.getCreatedAt());
        assertNull(api2.getServiceAddress());
    }
}
