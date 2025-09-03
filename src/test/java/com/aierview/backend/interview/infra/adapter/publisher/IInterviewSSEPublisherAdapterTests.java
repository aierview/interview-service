package com.aierview.backend.interview.infra.adapter.publisher;

import com.aierview.backend.interview.domain.contract.SSE.IInterviewSSEPublisher;
import com.aierview.backend.interview.domain.model.CurrentQuestion;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class IInterviewSSEPublisherAdapterTests {
    private IInterviewSSEPublisher interviewWebSocketPublisher;
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        this.simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        this.interviewWebSocketPublisher = null;
    }

    @Test
    @DisplayName("Should cond topic to /topic/interview/{interviewId}")
    void shouldCondTopicToTopicToInterview() {
        Long interviewId = 1L;
        CurrentQuestion currentQuestion = InterviewTestFixture.anyCurrentQuestion();
        Mockito.doNothing().when(this.simpMessagingTemplate).convertAndSend("/topic/interview/" + interviewId, currentQuestion);
//        this.interviewWebSocketPublisher.execute(interviewId, currentQuestion);
        Mockito.verify(simpMessagingTemplate, Mockito.times(1)).convertAndSend("/topic/interview/" + interviewId, currentQuestion);
    }
}
