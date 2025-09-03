package com.aierview.backend.interview.infra.adapter.stream.SSE;

import com.aierview.backend.interview.domain.contract.stream.SSE.ICreateSSEStream;
import com.aierview.backend.interview.domain.contract.stream.SSE.IInterviewSSEEmitter;
import com.aierview.backend.interview.domain.contract.stream.SSE.IPublishSSEEvent;
import com.aierview.backend.interview.domain.contract.stream.SSE.IRemoveSSEConnection;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InterviewSSEPublisherAdapter implements ICreateSSEStream, IRemoveSSEConnection, IPublishSSEEvent {
    private final Map<Long, List<IInterviewSSEEmitter>> connections = new ConcurrentHashMap<>();

    @Override
    public IInterviewSSEEmitter create(Long interviewId) {
        SseEmitter emitter = new SseEmitter(0L);
        IInterviewSSEEmitter interviewSSEEmitter = new InterviewSSEEmitterAdapter(emitter);
        connections.computeIfAbsent(interviewId, id -> new CopyOnWriteArrayList<>()).add(interviewSSEEmitter);
        return interviewSSEEmitter;
    }

    @Override
    public void publish(Long interviewId, Object event) {
        List<IInterviewSSEEmitter> emitters = connections.getOrDefault(interviewId, List.of());
        for (IInterviewSSEEmitter emitter : emitters) {
            try {
                emitter.handleSend(SseEmitter.event().name("next-question").data(event));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }

    @Override
    public void remove(Long interviewId, IInterviewSSEEmitter stream) {
        InterviewSSEEmitterAdapter emitterAdapter = (InterviewSSEEmitterAdapter) stream;
        connections.getOrDefault(interviewId, List.of()).remove((IInterviewSSEEmitter) emitterAdapter.getEmitter());
    }
}
