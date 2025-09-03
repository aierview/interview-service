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
import com.aierview.backend.interview.domain.model.BeginInterviewRequest;
import com.aierview.backend.interview.domain.model.InterviewEventPublisherPayload;
import com.aierview.backend.interview.usecase.contract.IBeginInterview;

import java.util.List;

public class BeginInterview implements IBeginInterview {
    private final IGetLoggedUser getLoggedUser;
    private final IInterviewRepository interviewRepository;
    private final IGenerateQuestions generateQuestions;
    private final IQuestionRepository questionRepository;
    private final IInterviewCacheRepository interviewCacheRepository;
    private final IInterviewEventPublisher interviewEventPublisher;

    public BeginInterview(IGetLoggedUser getLoggedUser, IInterviewRepository interviewRepository,
                          IGenerateQuestions generateQuestions, IQuestionRepository questionRepository,
                          IInterviewCacheRepository interviewCacheRepository, IInterviewEventPublisher interviewEventPublisher) {
        this.getLoggedUser = getLoggedUser;
        this.interviewRepository = interviewRepository;
        this.generateQuestions = generateQuestions;
        this.questionRepository = questionRepository;
        this.interviewCacheRepository = interviewCacheRepository;
        this.interviewEventPublisher = interviewEventPublisher;
    }

    @Override
    public Long execute(BeginInterviewRequest request) {
        UserRef user = this.getLoggedUser.execute();
        Interview interview = this.buildInterview(request, user);
        interview = this.interviewRepository.save(interview);
        List<Question> questions = this.generateQuestions.execute(request, interview.getId());
        questions = this.questionRepository.saveAll(questions);
        interview.setStatus(InterviewStatus.STARTED);
        interview = this.interviewRepository.update(interview);
        interview.setQuestions(questions);
        this.interviewCacheRepository.put(interview);
        Question nextQuestion = interview.getQuestions().getFirst();
        InterviewEventPublisherPayload payload = new InterviewEventPublisherPayload(nextQuestion.getId(), nextQuestion.getQuestion());
        this.interviewEventPublisher.publish(payload);
        return interview.getId();
    }

    private Interview buildInterview(BeginInterviewRequest request, UserRef user) {
        return Interview
                .builder()
                .stack(request.getStack())
                .user(user)
                .role(request.getRole())
                .level(request.getInterviewLevel())
                .status(InterviewStatus.CREATED)
//                .createdAt(LocalDateTime.now())
                .build();
    }
}
