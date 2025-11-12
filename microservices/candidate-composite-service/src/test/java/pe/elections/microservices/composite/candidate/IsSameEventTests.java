package pe.elections.microservices.composite.candidate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static pe.elections.microservices.composite.candidate.IsSameEvent.sameEventExceptCreatedAt;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.event.Event.Type;

class IsSameEventTests {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventObjectCompare() throws JsonProcessingException {
        Event<Integer, Candidate> event1 = new Event<>(Type.CREATE, 1, new Candidate(1, "name", 30, null));
        Event<Integer, Candidate> event2 = new Event<>(Type.CREATE, 1, new Candidate(1, "name", 30, null));
        Event<Integer, Candidate> event3 = new Event<>(Type.DELETE, 1, null);
        Event<Integer, Candidate> event4 = new Event<>(Type.CREATE, 1, new Candidate(2, "name", 30, null));
        String event1Json = mapper.writeValueAsString(event1);
        assertThat(event1Json, is(sameEventExceptCreatedAt(event2)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event3)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event4)));
    }
}
