package com.aierview.backend.interview.domain.contract.stream.SSE;

public interface ICreateSSEStream {
    IInterviewSSEEmitter create(Long interviewId);
}
