package pe.elections.microservices.api.composite.candidate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@SecurityRequirement(name = "security_auth")
@Tag(name = "CandidateComposite", description = "REST API for composite product information.")
public interface CandidateCompositeService {

    @Operation(
        summary = "{}",
        description = "{}"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "{}"),
        @ApiResponse(responseCode = "422", description = "{}"),
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(
        value = "/candidate-composite",
        consumes = "application/json"
    )
    Mono<Void> createCandidate(@RequestBody CandidateAggregate body);

    @Operation(
        summary = "${api.candidate-composite.get-composite-candidate.description}",
        description = "${api.candidate-composite.get-composite-candidate.notes}"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(
        value = "/candidate-composite/{candidateId}",
        produces = "application/json"
    )
    Mono<CandidateAggregate> getCandidate(@PathVariable int candidateId);

    @Operation(
        summary = "{}",
        description = "{}"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "{}"),
        @ApiResponse(responseCode = "422", description = "{}"),
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/candidate-composite/{candidateId}")
    Mono<Void> deleteCandidate(@PathVariable int candidateId);

}
