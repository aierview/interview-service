package com.aierview.backend.interview.infra.adapter.publisher;

import com.aierview.backend.interview.domain.contract.stream.IInterviewEventConsumer;
import com.aierview.backend.interview.domain.model.InterviewEventConsumerPayload;
import com.aierview.backend.interview.infra.adapter.stream.KafkaInterviewEventConsumerAdapter;
import com.aierview.backend.interview.usecase.contract.ISendCurrentQuestion;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class KafkaKafkaInterviewEventConsumerAdapterTests {
    private IInterviewEventConsumer kafkaInterviewEventConsumer;
    private ISendCurrentQuestion sendCurrentQuestion;

    @BeforeEach
    void setUp() {
        this.sendCurrentQuestion = Mockito.mock(ISendCurrentQuestion.class);
        this.kafkaInterviewEventConsumer = new KafkaInterviewEventConsumerAdapter(sendCurrentQuestion);
    }


    @Test
    @DisplayName("should consume topic of kafka and send question")
    void shouldConsumeTopicOfKafkaAndSendQuestion() {
        InterviewEventConsumerPayload currentQuestion = InterviewTestFixture.anyInterviewEventConsumerPayload();
        Mockito.doNothing().when(this.sendCurrentQuestion).execute(currentQuestion);
        this.kafkaInterviewEventConsumer.consume(currentQuestion);
        Mockito.verify(this.sendCurrentQuestion, Mockito.times(1)).execute(currentQuestion);

    }
}
