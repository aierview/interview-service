package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.interview.domain.contract.IA.IGenerateQuestions;
import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.contract.repository.IInterviewRepository;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.contract.stream.IInterviewEventPublisher;
import com.aierview.backend.interview.domain.contract.user.IGetLoggedUser;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.enums.InterviewStatus;
import com.aierview.backend.interview.domain.exceptions.UnavailableIAServiceException;
import com.aierview.backend.interview.domain.exceptions.UserNotAuthenticatedException;
import com.aierview.backend.interview.domain.model.BeginInterviewRequest;
import com.aierview.backend.interview.usecase.contract.IBeginInterview;
import com.aierview.backend.shared.testdata.AuthTestFixture;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BeginInterviewTests {
    private IBeginInterview beginInterview;
    private IGetLoggedUser getLoggedUser;
    private IInterviewRepository interviewRepository;
    private IGenerateQuestions generateQuestions;
    private IQuestionRepository questionRepository;
    private IInterviewCacheRepository interviewCacheRepository;
    private IInterviewEventPublisher interviewEventPublisher;

    @BeforeEach
    void setUp() {
        this.getLoggedUser = mock(IGetLoggedUser.class);
        this.interviewRepository = mock(IInterviewRepository.class);
        this.generateQuestions = mock(IGenerateQuestions.class);
        this.questionRepository = mock(IQuestionRepository.class);
        this.interviewCacheRepository = mock(IInterviewCacheRepository.class);
        this.interviewEventPublisher = mock(IInterviewEventPublisher.class);
        this.beginInterview = new BeginInterview(getLoggedUser, interviewRepository,
                generateQuestions, questionRepository, interviewCacheRepository, interviewEventPublisher);
    }

    @Test
    @DisplayName("Should throw UserNotAuthenticatedException if user is not authenticate")
    void shouldThrowUserNotAuthenticatedExceptionIfUserIsNotAuthenticated() {
        BeginInterviewRequest request = InterviewTestFixture.anyBeginInterviewRequest();

        when(this.getLoggedUser.execute()).thenThrow(new UserNotAuthenticatedException());

        Throwable exception = Assertions.catchThrowable(() -> this.beginInterview.execute(request));

        assertThat(exception).isInstanceOf(UserNotAuthenticatedException.class);
        assertThat(exception.getMessage()).isEqualTo("User is not authenticated!");
        verify(this.getLoggedUser, times(1)).execute();
    }

    @Test
    @DisplayName("Should throw UnavailableIAServiceException if generate interview throws")
    void shouldThrowUnavailableIAServiceExceptionIfGenerateInterviewThrows() {
        BeginInterviewRequest request = InterviewTestFixture.anyBeginInterviewRequest();
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview interviewWithNoQuestion = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);
        Interview savedInterviewWithNoQuestion = InterviewTestFixture.anySavedInterviewWithNoQuestions(interviewWithNoQuestion);

        when(this.getLoggedUser.execute()).thenReturn(savedUser);
        when(this.interviewRepository.save(any(Interview.class)))
                .thenAnswer(invocation -> {
                    Interview interview = invocation.getArgument(0);
                    return Interview.builder()
                            .id(1L)
                            .stack(interview.getStack())
                            .user(interview.getUser())
                            .role(interview.getRole())
                            .level(interview.getLevel())
                            .status(interview.getStatus())
//                            .createdAt(interview.getCreatedAt())
                            .build();
                });
        when(this.generateQuestions.execute(request, savedInterviewWithNoQuestion.getId())).thenThrow(new UnavailableIAServiceException());

        Throwable exception = Assertions.catchThrowable(() -> this.beginInterview.execute(request));

        assertThat(exception).isInstanceOf(UnavailableIAServiceException.class);
        assertThat(exception.getMessage()).isEqualTo("We sorry! IA Service not available at this time, please try again later.");
        verify(this.getLoggedUser, times(1)).execute();
        verify(this.interviewRepository).save(any(Interview.class));
        verify(this.generateQuestions, times(1)).execute(request, savedInterviewWithNoQuestion.getId());
    }

    @Test
    @DisplayName("Should publish first question when begin interview succeeds")
    void shouldPublishFirstQuestionWhenBeginInterviewSucceeds() {
        BeginInterviewRequest request = InterviewTestFixture.anyBeginInterviewRequest();
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview interviewWithNoQuestion = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);
        Interview savedInterviewWithNoQuestion = InterviewTestFixture.anySavedInterviewWithNoQuestions(interviewWithNoQuestion);

        List<Question> questions = InterviewTestFixture.anyQuestionList(savedInterviewWithNoQuestion);
        List<Question> savedQuestions = InterviewTestFixture.anySavedQuestionList(savedInterviewWithNoQuestion);

        Interview savedInterviewWithQuestion = InterviewTestFixture.anySavedStartedInterviewWithQuestions(savedInterviewWithNoQuestion, questions);

        when(this.getLoggedUser.execute()).thenReturn(savedUser);
        when(this.interviewRepository.save(any(Interview.class)))
                .thenAnswer(invocation -> {
                    Interview interview = invocation.getArgument(0);
                    return Interview.builder()
                            .id(1L)
                            .stack(interview.getStack())
                            .user(interview.getUser())
                            .role(interview.getRole())
                            .level(interview.getLevel())
                            .status(interview.getStatus())
//                            .createdAt(interview.getCreatedAt())
                            .build();
                });

        when(this.generateQuestions.execute(request, savedInterviewWithNoQuestion.getId())).thenReturn(questions);
        when(this.questionRepository.saveAll(questions)).thenReturn(savedQuestions);
        when(this.interviewRepository.update(any(Interview.class))).thenReturn(savedInterviewWithQuestion);
        doNothing().when(this.interviewCacheRepository).put(savedInterviewWithQuestion);
        doNothing().when(this.interviewEventPublisher).publish(Mockito.any());

        this.beginInterview.execute(request);

        verify(this.getLoggedUser, times(1)).execute();
        verify(this.interviewRepository).save(any(Interview.class));
        verify(this.generateQuestions, times(1)).execute(request, savedInterviewWithNoQuestion.getId());
        verify(this.questionRepository, times(1)).saveAll(questions);
        verify(this.interviewRepository).update(argThat(interview ->
                        interview.getId().equals(interview.getId()) &&
                                interview.getUser().equals(savedUser) &&
                                interview.getRole() == interviewWithNoQuestion.getRole() &&
                                interview.getLevel() == interviewWithNoQuestion.getLevel() &&
                                interview.getStack().equals(interviewWithNoQuestion.getStack()) &&
                                interview.getStatus() == InterviewStatus.STARTED
//                        interview.getCreatedAt() != null
        ));
    }
}
