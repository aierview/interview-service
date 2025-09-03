package com.aierview.backend.interview.usecase.impl;

import com.aierview.backend.interview.domain.contract.IA.IIAGenerateFeedback;
import com.aierview.backend.interview.domain.contract.repository.IQuestionRepository;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableNextQuestionException;
import com.aierview.backend.interview.domain.model.AnswerEventConsumerPayload;
import com.aierview.backend.interview.domain.model.GenerateFeedbackRequest;
import com.aierview.backend.interview.domain.model.GenerateFeedbackResponse;
import com.aierview.backend.interview.usecase.contract.IGenerateFeedback;

public class GenerateFeedback implements IGenerateFeedback {
    private final IQuestionRepository questionRepository;
    private final IIAGenerateFeedback iiaGenerateFeedback;

    public GenerateFeedback(IQuestionRepository questionRepository, IIAGenerateFeedback iiaGenerateFeedback) {
        this.questionRepository = questionRepository;
        this.iiaGenerateFeedback = iiaGenerateFeedback;
    }

    @Override
    public void execute(AnswerEventConsumerPayload payload) {
        Question question = this.questionRepository.findById(payload.questionId()).orElseThrow(UnavailableNextQuestionException::new);
        Interview interview = question.getInterview();
        GenerateFeedbackRequest request = new GenerateFeedbackRequest
                (question.getQuestion(), payload.answerText(), interview.getRole(), interview.getLevel(), interview.getStack());
        GenerateFeedbackResponse feedback = this.iiaGenerateFeedback.execute(request);
        question.setAnswer(payload.answerText());
        question.setFeedback(feedback.feedback());
        question.setScore(feedback.score());
        this.questionRepository.save(question);
    }
}
