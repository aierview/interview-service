package com.aierview.backend.interview.usecase.contract;

import com.aierview.backend.interview.domain.contract.stream.SSE.IInterviewSSEEmitter;

public interface ISubscribeInterviewSSE {
    IInterviewSSEEmitter execute(Long interviewId);
}
