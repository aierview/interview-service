package com.aierview.backend.interview.domain.contract.stream.SSE;

public interface IRemoveSSEConnection {
    void remove(Long interviewId, IInterviewSSEEmitter stream);
}
