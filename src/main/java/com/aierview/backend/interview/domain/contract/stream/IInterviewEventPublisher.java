package com.aierview.backend.interview.domain.contract.stream;

import com.aierview.backend.interview.domain.model.InterviewEventPublisherPayload;

public interface IInterviewEventPublisher {
    void publish(InterviewEventPublisherPayload payload);
}
