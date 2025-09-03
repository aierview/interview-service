package com.aierview.backend.interview.domain.contract.stream;

import com.aierview.backend.interview.domain.model.AnswerEventPublisherPayload;

public interface IAnswerEventPublisher {
    void publish(AnswerEventPublisherPayload payload);
}
