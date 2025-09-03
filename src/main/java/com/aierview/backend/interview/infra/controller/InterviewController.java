package com.aierview.backend.interview.infra.controller;

import com.aierview.backend.auth.domain.model.http.Response;
import com.aierview.backend.interview.domain.model.AnswerQuestionRequest;
import com.aierview.backend.interview.domain.model.BeginInterviewRequest;
import com.aierview.backend.interview.infra.adapter.stream.SSE.InterviewSSEEmitterAdapter;
import com.aierview.backend.interview.usecase.contract.IAnswerQuestion;
import com.aierview.backend.interview.usecase.contract.IBeginInterview;
import com.aierview.backend.interview.usecase.contract.ISubscribeInterviewSSE;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interview")
@Tag(name = "Interview", description = "Interview Features")
public class InterviewController {
    private final IBeginInterview beginInterviewUseCase;
    private final IAnswerQuestion answerQuestionUseCase;
    private final ISubscribeInterviewSSE subscribeInterviewSse;


    @PostMapping("/begin")
    @Operation(summary = "Begin Interview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "ACCEPTED"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Response> beginInterviewUse(@Valid @RequestBody BeginInterviewRequest request) {
        Long interviewId = this.beginInterviewUseCase.execute(request);
        Response response = Response.builder().data(interviewId).statusCode(HttpStatus.ACCEPTED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/answer-question")
    @Operation(summary = "Answer a Question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
    })
    public ResponseEntity<Response> onAnswerReceived(@Valid @RequestBody AnswerQuestionRequest request) {
        this.answerQuestionUseCase.execute(request);
        Response response = Response.builder().statusCode(HttpStatus.OK.value()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{interviewId}/subscribe")
    public SseEmitter subscribe(@PathVariable Long interviewId) {
        InterviewSSEEmitterAdapter emitterAdapter = (InterviewSSEEmitterAdapter) subscribeInterviewSse.execute(interviewId);
        return emitterAdapter.getEmitter();
    }
}
