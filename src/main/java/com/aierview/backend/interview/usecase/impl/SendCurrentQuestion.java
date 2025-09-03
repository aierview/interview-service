package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.contract.stream.IInterviewEventPublisher;
import com.aierview.backend.interview.domain.contract.stream.SSE.IPublishSSEEvent;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableNextQuestionException;
import com.aierview.backend.interview.domain.model.CurrentQuestion;
import com.aierview.backend.interview.domain.model.InterviewEventConsumerPayload;
import com.aierview.backend.interview.domain.model.InterviewEventPublisherPayload;
import com.aierview.backend.interview.usecase.contract.ISendCurrentQuestion;

public class SendCurrentQuestion implements ISendCurrentQuestion {
    private final IPublishSSEEvent publishSSEEvent;
    private final IInterviewCacheRepository interviewCacheRepository;
    private final IQuestionRepository questionRepository;
    private final IInterviewEventPublisher interviewEventPublisher;

    public SendCurrentQuestion(IPublishSSEEvent publishSSEEvent, IInterviewCacheRepository interviewCacheRepository, IQuestionRepository questionRepository, IInterviewEventPublisher interviewEventPublisher) {
        this.publishSSEEvent = publishSSEEvent;
        this.interviewCacheRepository = interviewCacheRepository;
        this.questionRepository = questionRepository;
        this.interviewEventPublisher = interviewEventPublisher;
    }

    @Override
    public void execute(InterviewEventConsumerPayload payload) {
        Question existingQuestion = this.questionRepository.findById(payload.questionId())
                .orElseThrow(UnavailableNextQuestionException::new);

        existingQuestion.setAudioUrl(payload.audioUrl());
        this.questionRepository.save(existingQuestion);
        Interview interview = existingQuestion.getInterview();
        InterviewState interviewState = this.interviewCacheRepository.get(interview.getId());
        CurrentQuestion currentQuestion = new CurrentQuestion(existingQuestion.getId(), existingQuestion.getQuestion(), existingQuestion.getAudioUrl());
        this.publishSSEEvent.publish(interview.getId(), currentQuestion);
        interviewState.setStatus(existingQuestion.getId(), "WAITING_FOR_CLIENT_ACK", existingQuestion);
        this.interviewCacheRepository.revalidate(interviewState.getInterviewId(), interviewState);

        //Send next question to be processed by tts
        if (interviewState.hasNextQuestion()) {
            Question nextQuestion = interviewState.peekNextQuestion();
            InterviewEventPublisherPayload PublishPayload = new InterviewEventPublisherPayload(nextQuestion.getId(), nextQuestion.getQuestion());
            this.interviewEventPublisher.publish(PublishPayload);
            interviewState.setStatus(nextQuestion.getId(), "WAITING_FOR_PREP", nextQuestion);
            interviewState.advanceToNextQuestion();
            this.interviewCacheRepository.revalidate(interview.getId(), interviewState);
        }
    }
}
