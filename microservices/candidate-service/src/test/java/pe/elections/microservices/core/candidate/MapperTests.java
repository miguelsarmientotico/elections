package pe.elections.microservices.core.candidate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.core.candidate.persistence.CandidateEntity;
import pe.elections.microservices.core.candidate.services.CandidateMapper;

class MapperTests {
    private CandidateMapper mapper = Mappers.getMapper(CandidateMapper.class);

    @Test
    void mapperTests() {
        assertNotNull(mapper);
        Candidate api = new Candidate(1, "name Candidato", 30, "adr");
        CandidateEntity entity = mapper.apiToEntity(api);
        assertEquals(api.getCandidateId(), entity.getCandidateId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getEdad(), entity.getEdad());
        Candidate api2 = mapper.entityToApi(entity);
        assertEquals(api.getCandidateId(), api2.getCandidateId());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getEdad(), api2.getEdad());
        assertNull(api2.getServiceAddress());
    }
}
