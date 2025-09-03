package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.interview.domain.contract.stream.SSE.IInterviewSSEEmitter;
import com.aierview.backend.interview.infra.adapter.stream.SSE.InterviewSSEPublisherAdapter;
import com.aierview.backend.interview.usecase.contract.ISubscribeInterviewSSE;

public class SubscribeInterviewSSE implements ISubscribeInterviewSSE {
    private final InterviewSSEPublisherAdapter publisher;

    public SubscribeInterviewSSE(InterviewSSEPublisherAdapter publisher) {
        this.publisher = publisher;
    }

    @Override
    public IInterviewSSEEmitter execute(Long interviewId) {
        IInterviewSSEEmitter stream = publisher.create(interviewId);
        stream.handleClose(() -> publisher.remove(interviewId, stream));
        stream.handleError(() -> publisher.remove(interviewId, stream));
        return stream;
    }
}
