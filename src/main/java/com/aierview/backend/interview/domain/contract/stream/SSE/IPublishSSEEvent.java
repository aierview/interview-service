package com.aierview.backend.interview.domain.contract.stream.SSE;

public interface IPublishSSEEvent {
    void publish(Long interviewId, Object event);
}
