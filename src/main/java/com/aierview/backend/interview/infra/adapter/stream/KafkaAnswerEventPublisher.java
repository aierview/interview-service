package com.aierview.backend.interview.infra.adapter.stream;

import com.aierview.backend.interview.domain.contract.stream.IAnswerEventPublisher;
import com.aierview.backend.interview.domain.model.AnswerEventPublisherPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAnswerEventPublisher implements IAnswerEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(AnswerEventPublisherPayload payload) {
        kafkaTemplate.send("interview-answer.audio", payload);
    }
}
