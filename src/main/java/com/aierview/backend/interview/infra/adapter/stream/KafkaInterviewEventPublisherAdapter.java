package com.aierview.backend.interview.infra.adapter.stream;

import com.aierview.backend.interview.domain.contract.stream.IInterviewEventPublisher;
import com.aierview.backend.interview.domain.model.InterviewEventPublisherPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaInterviewEventPublisherAdapter implements IInterviewEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(InterviewEventPublisherPayload payload) {
        kafkaTemplate.send("interview-question.text", payload);
    }
}
