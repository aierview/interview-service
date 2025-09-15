package com.aierview.backend.interview.infra.adapter.stream;

import com.aierview.backend.interview.domain.contract.stream.IInterviewEventConsumer;
import com.aierview.backend.interview.domain.model.InterviewEventConsumerPayload;
import com.aierview.backend.interview.usecase.contract.ISendCurrentQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaInterviewEventConsumerAdapter implements IInterviewEventConsumer {
    private final ISendCurrentQuestion sendCurrentQuestion;

    @Override
    @KafkaListener(topics = "interview-question.audio", groupId = "interview-tts-service-group", containerFactory = "ttsKafkaListenerFactory")
    public void consume(InterviewEventConsumerPayload payload,  @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        this.sendCurrentQuestion.execute(payload);
    }
}
