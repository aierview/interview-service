package com.aierview.backend.interview.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerQuestionRequest(
        @NotNull(message = "Question id is required") Long questionId,
        @NotBlank(message = "Please provide an answer") String base64Answer) {
}
