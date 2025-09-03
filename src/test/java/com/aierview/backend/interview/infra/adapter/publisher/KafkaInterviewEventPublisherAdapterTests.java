package com.aierview.backend.interview.infra.adapter.publisher;

import com.aierview.backend.interview.domain.contract.stream.IInterviewEventPublisher;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.model.InterviewEventPublisherPayload;
import com.aierview.backend.interview.infra.adapter.stream.KafkaInterviewEventPublisherAdapter;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;


public class KafkaInterviewEventPublisherAdapterTests {
    private IInterviewEventPublisher interviewEventPublisher;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        this.kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        this.interviewEventPublisher = new KafkaInterviewEventPublisherAdapter(kafkaTemplate);
    }

    @Test
    @DisplayName("Should publish first question on topic first-question-topic")
    void shouldPublishFirstQuestionOnTopicTopic() {
        Question question = InterviewTestFixture.anyQuestion();
        question.setId(1L);
        question.setInterview(Interview.builder().id(1L).build());

        InterviewEventPublisherPayload payload = new InterviewEventPublisherPayload(question.getId(), question.getQuestion());

        SendResult<String, Object> fakeResult =
                new SendResult<>(null, new RecordMetadata(null, 0, 0, 0, Long.valueOf(0), 0, 0));

        Mockito.when(kafkaTemplate.send("first-question-topic", payload))
                .thenReturn(CompletableFuture.completedFuture(fakeResult));

        this.interviewEventPublisher.publish(payload);
        Mockito.verify(this.kafkaTemplate, Mockito.times(1)).send("interview.next-question-text", payload);
    }
}
