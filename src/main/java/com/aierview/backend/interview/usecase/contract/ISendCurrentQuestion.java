package com.aierview.backend.interview.usecase.contract;

import com.aierview.backend.interview.domain.model.InterviewEventConsumerPayload;

public interface ISendCurrentQuestion {
    void execute(InterviewEventConsumerPayload payload);
}
