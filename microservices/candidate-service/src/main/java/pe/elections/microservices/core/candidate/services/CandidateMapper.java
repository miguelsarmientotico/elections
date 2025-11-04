package pe.elections.microservices.core.candidate.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.core.candidate.persistence.CandidateEntity;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Candidate entityToApi(CandidateEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    CandidateEntity apiToEntity(Candidate candidate);
}
