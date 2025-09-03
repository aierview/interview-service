package com.aierview.backend.interview.domain.contract.stream.SSE;

public interface IInterviewSSEEmitter {
    void handleSend(Object event);

    void handleError(Runnable callback);

    void handleClose(Runnable callback);
}
