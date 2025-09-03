package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.interview.domain.contract.bucket.IUploadBase64File;
import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.contract.stream.IAnswerEventPublisher;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableNextQuestionException;
import com.aierview.backend.interview.domain.model.AnswerQuestionRequest;
import com.aierview.backend.interview.usecase.contract.IAnswerQuestion;
import com.aierview.backend.shared.testdata.AuthTestFixture;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class OnAnswerReceivedTests {
    private IAnswerQuestion onAnswerReceived;
    private IQuestionRepository questionRepository;
    private IInterviewCacheRepository interviewCacheRepository;
    private IAnswerEventPublisher answerEventPublisher;
    private IUploadBase64File uploadBase64File;

    @BeforeEach
    void setUp() {
        this.questionRepository = Mockito.mock(IQuestionRepository.class);
        this.interviewCacheRepository = Mockito.mock(IInterviewCacheRepository.class);
        this.uploadBase64File = Mockito.mock(IUploadBase64File.class);
        this.onAnswerReceived = new AnswerQuestion(questionRepository, interviewCacheRepository, answerEventPublisher, uploadBase64File);
    }


    @Test
    @DisplayName("Should throw UnavailableNextQuestionException if user does not exists")
    void shouldThrowUnavailableNextQuestionException() {
        AnswerQuestionRequest request = InterviewTestFixture.anyOnQuestionOnAnswerReceivedRequest();
        Mockito.when(this.questionRepository.findById(request.questionId())).thenReturn(Optional.empty());
        Throwable exception = Assertions.catchThrowable(() -> this.onAnswerReceived.execute(request));
        Assertions.assertThat(exception).isInstanceOf(UnavailableNextQuestionException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("We sorry! We couldn't provide next question. Please try again.");
    }

    @Test
    @DisplayName("Should publish netx question to websocket")
    void shouldPublishNetxQuestionToWebsocket() {
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview toSaveInterview = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);
        Interview savedInterview = InterviewTestFixture.anySavedInterviewWithNoQuestions(toSaveInterview);

        List<Question> questions = InterviewTestFixture.anySavedQuestionList(savedInterview);
        AnswerQuestionRequest request = InterviewTestFixture.anyOnQuestionOnAnswerReceivedRequest();

        InterviewState interviewState = InterviewTestFixture.anySavedInterviewState(savedInterview.getId(), questions);

        Mockito.when(this.questionRepository.findById(request.questionId())).thenReturn(Optional.of(questions.getFirst()));
        Mockito.when(this.interviewCacheRepository.get(interviewState.getInterviewId())).thenReturn(interviewState);

        this.onAnswerReceived.execute(request);

        Mockito.verify(this.questionRepository, Mockito.times(1)).findById(request.questionId());
        Mockito.verify(this.interviewCacheRepository, Mockito.times(1)).get(interviewState.getInterviewId());

    }
}
