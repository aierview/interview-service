package com.aierview.backend.interview.infra.adapter.stream;

import com.aierview.backend.interview.domain.contract.stream.IAnswerEventConsumer;
import com.aierview.backend.interview.domain.model.AnswerEventConsumerPayload;
import com.aierview.backend.interview.usecase.contract.IGenerateFeedback;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAnswerEventConsumer implements IAnswerEventConsumer {
    private final IGenerateFeedback generateFeedback;

    @Override
    @KafkaListener(topics = "interview-answer.text", groupId = "interview-tts-service-group", containerFactory = "sttKafkaListenerFactory")
    public void consume(AnswerEventConsumerPayload payload) {
        this.generateFeedback.execute(payload);
    }
}
