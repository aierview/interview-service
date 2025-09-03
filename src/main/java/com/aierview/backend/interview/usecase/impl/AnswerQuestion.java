package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.interview.domain.contract.bucket.IUploadBase64File;
import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.contract.stream.IAnswerEventPublisher;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableNextQuestionException;
import com.aierview.backend.interview.domain.model.AnswerEventPublisherPayload;
import com.aierview.backend.interview.domain.model.AnswerQuestionRequest;
import com.aierview.backend.interview.usecase.contract.IAnswerQuestion;

public class AnswerQuestion implements IAnswerQuestion {
    private final IQuestionRepository questionRepository;
    private final IInterviewCacheRepository interviewCacheRepository;
    private final IAnswerEventPublisher answerEventPublisher;
    private final IUploadBase64File uploadBase64File;

    public AnswerQuestion(IQuestionRepository questionRepository, IInterviewCacheRepository interviewCacheRepository, IAnswerEventPublisher answerEventPublisher, IUploadBase64File uploadBase64File) {
        this.questionRepository = questionRepository;
        this.interviewCacheRepository = interviewCacheRepository;
        this.answerEventPublisher = answerEventPublisher;
        this.uploadBase64File = uploadBase64File;
    }

    @Override
    public void execute(AnswerQuestionRequest request) {
        Question existingQuestion = this.questionRepository.findById(request.questionId())
                .orElseThrow(UnavailableNextQuestionException::new);
        Interview interview = existingQuestion.getInterview();
        InterviewState interviewState = this.interviewCacheRepository.get(interview.getId());
        interviewState.setStatus(existingQuestion.getId(), "ANSWERED", existingQuestion);
        this.interviewCacheRepository.revalidate(interview.getId(), interviewState);
        String filename = this.uploadBase64File.execute(request.base64Answer());
        AnswerEventPublisherPayload payload = new AnswerEventPublisherPayload(existingQuestion.getId(), filename);
        this.answerEventPublisher.publish(payload);
    }
}
