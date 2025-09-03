package com.aierview.backend.interview.usecase.contract;

import com.aierview.backend.interview.domain.model.AnswerQuestionRequest;

public interface IAnswerQuestion {
    void execute(AnswerQuestionRequest request);
}
