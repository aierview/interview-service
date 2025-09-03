package com.aierview.backend.interview.infra.adapter.stream.SSE;

import com.aierview.backend.interview.domain.contract.stream.SSE.IInterviewSSEEmitter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
public class InterviewSSEEmitterAdapter implements IInterviewSSEEmitter {
    @Getter
    private final SseEmitter emitter;
    private Runnable onErrorCallback;

    @Override
    public void handleSend(Object event) {
        try {
            emitter.send(SseEmitter.event().data(event));
        } catch (Exception e) {
            if (onErrorCallback != null) onErrorCallback.run();
        }
    }

    @Override
    public void handleError(Runnable callback) {
        emitter.onCompletion(callback);
    }

    @Override
    public void handleClose(Runnable callback) {
        this.onErrorCallback = callback;
        emitter.onTimeout(callback);
    }
}
