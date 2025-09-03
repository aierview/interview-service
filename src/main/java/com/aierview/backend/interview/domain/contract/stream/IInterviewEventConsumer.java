package com.aierview.backend.interview.domain.contract.stream;

import com.aierview.backend.interview.domain.model.InterviewEventConsumerPayload;

public interface IInterviewEventConsumer {
    void consume(InterviewEventConsumerPayload payload);
}
