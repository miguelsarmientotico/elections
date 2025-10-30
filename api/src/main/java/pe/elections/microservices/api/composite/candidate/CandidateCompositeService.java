package pe.elections.microservices.api.composite.candidate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CandidateComposite", description = "REST API for composite product information.")
public interface CandidateCompositeService {

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
    CandidateAggregate getCandidate(@PathVariable int candidateId);
}
