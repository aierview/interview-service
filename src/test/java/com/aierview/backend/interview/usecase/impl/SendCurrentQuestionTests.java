package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.contract.stream.IInterviewEventPublisher;
import com.aierview.backend.interview.domain.contract.stream.SSE.IPublishSSEEvent;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableNextQuestionException;
import com.aierview.backend.interview.domain.model.CurrentQuestion;
import com.aierview.backend.interview.usecase.contract.ISendCurrentQuestion;
import com.aierview.backend.shared.testdata.AuthTestFixture;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class SendCurrentQuestionTests {
    private ISendCurrentQuestion sendCurrentQuestion;
    private IPublishSSEEvent publishSSEEvent;
    private IInterviewCacheRepository interviewCacheRepository;
    private IQuestionRepository questionRepository;
    private IInterviewEventPublisher interviewEventPublisher;


    @BeforeEach
    public void setup() {
        this.publishSSEEvent = mock(IPublishSSEEvent.class);
        this.interviewCacheRepository = mock(IInterviewCacheRepository.class);
        this.questionRepository = mock(IQuestionRepository.class);
        sendCurrentQuestion = new SendCurrentQuestion(publishSSEEvent, interviewCacheRepository, questionRepository, interviewEventPublisher);
    }

    @Test
    @DisplayName("Should throw UnavailableNextQuestionException when question not found")
    void shouldThrowUnavailableNextQuestionExceptionWhenQuestionNotFound() {
        CurrentQuestion currentQuestion = InterviewTestFixture.anyCurrentQuestion();

        when(questionRepository.findById(currentQuestion.questionId())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() -> sendCurrentQuestion.execute(Mockito.any()));

        Assertions.assertThat(exception).isInstanceOf(UnavailableNextQuestionException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("We sorry! We couldn't provide next question. Please try again.");
        verify(this.questionRepository, times(1)).findById(currentQuestion.questionId());
    }

    @Test
    @DisplayName("Should publish first question to websocket")
    void shouldPublishFirstQuestionToWebSocket() {
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview toSaveInterview = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);
        Interview savedInterview = InterviewTestFixture.anySavedInterviewWithNoQuestions(toSaveInterview);

        Question question = InterviewTestFixture.anySavedQuestion(savedInterview);
        CurrentQuestion currentQuestion = InterviewTestFixture.anyCurrentQuestion(question);
        Question questionWihAudioUrl = InterviewTestFixture.anySavedQuestion(question, currentQuestion.audioUrl());

        InterviewState interviewState = InterviewTestFixture.anySavedInterviewState(savedInterview, question);

        when(this.questionRepository.findById(currentQuestion.questionId())).thenReturn(Optional.of(question));
        when(this.questionRepository.save(questionWihAudioUrl)).thenReturn(questionWihAudioUrl);
        when(this.interviewCacheRepository.get(savedInterview.getId())).thenReturn(interviewState);

        this.sendCurrentQuestion.execute(Mockito.any());

        Mockito.verify(this.questionRepository, times(1)).findById(currentQuestion.questionId());
        Mockito.verify(this.questionRepository, times(1)).save(questionWihAudioUrl);
        Mockito.verify(this.interviewCacheRepository, times(1)).get(savedInterview.getId());
        Mockito.verify(this.publishSSEEvent, times(1)).publish(interviewState.getInterviewId(), currentQuestion);
        Mockito.verify(this.interviewCacheRepository, times(1)).revalidate(interviewState.getInterviewId(), interviewState);
    }

    @Test
    @DisplayName("Should set status to READY FOR SEND when is not the first question")
    void shouldSetStatusToReadyForSendWhenQuestionIsTheFirstQuestion() {
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview toSaveInterview = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);
        Interview savedInterview = InterviewTestFixture.anySavedInterviewWithNoQuestions(toSaveInterview);

        Question question = InterviewTestFixture.anySavedQuestion(savedInterview);
        CurrentQuestion currentQuestion = InterviewTestFixture.anyCurrentQuestion(question);
        Question questionWihAudioUrl = InterviewTestFixture.anySavedQuestion(question, currentQuestion.audioUrl());
        Question questionWihAudioUrl1 = InterviewTestFixture.anySavedQuestion(question, currentQuestion.audioUrl());

        InterviewState interviewState = InterviewTestFixture.anySavedInterviewState(savedInterview, question);
        InterviewState interviewStateWFCACK = InterviewTestFixture
                .anySavedInterviewState(interviewState, List.of(questionWihAudioUrl, questionWihAudioUrl1));

        when(this.questionRepository.findById(currentQuestion.questionId())).thenReturn(Optional.of(question));
        when(this.questionRepository.save(questionWihAudioUrl)).thenReturn(questionWihAudioUrl);
        when(this.interviewCacheRepository.get(savedInterview.getId())).thenReturn(interviewStateWFCACK);

        this.sendCurrentQuestion.execute(Mockito.any());

        Mockito.verify(this.questionRepository, times(1)).findById(currentQuestion.questionId());
        Mockito.verify(this.questionRepository, times(1)).save(questionWihAudioUrl);
        Mockito.verify(this.interviewCacheRepository, times(1)).get(savedInterview.getId());
        Mockito.verify(this.publishSSEEvent, times(0)).publish(interviewStateWFCACK.getInterviewId(), currentQuestion);
        Mockito.verify(this.interviewCacheRepository, times(1)).revalidate(interviewStateWFCACK.getInterviewId(), interviewStateWFCACK);

    }
}
