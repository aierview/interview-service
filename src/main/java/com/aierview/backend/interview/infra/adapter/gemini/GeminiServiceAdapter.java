package com.aierview.backend.interview.infra.adapter.gemini;

import com.aierview.backend.interview.domain.contract.IA.IGenerateQuestions;
import com.aierview.backend.interview.domain.contract.IA.IIAGenerateFeedback;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.exceptions.UnavailableIAServiceException;
import com.aierview.backend.interview.domain.model.BeginInterviewRequest;
import com.aierview.backend.interview.domain.model.GenerateFeedbackRequest;
import com.aierview.backend.interview.domain.model.GenerateFeedbackResponse;
import com.aierview.backend.shared.utils.GeminiFunctUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiServiceAdapter implements IGenerateQuestions, IIAGenerateFeedback {
    private final GeminiFunctUtils geminiFunctUtils;

    @Override
    public List<Question> execute(BeginInterviewRequest request, Long interviewId) {
        try {
            String prompt = this.geminiFunctUtils.generateQuestionsPrompt(request);
            List<String> questionsText = this.geminiFunctUtils.getResponse(prompt);
            Interview interviewRef = Interview.builder().id(interviewId).build();
            return questionsText.stream()
                    .filter(q -> !q.isEmpty())
                    .map(q -> Question
                            .builder().question(q).interview(interviewRef).build())
                    .toList();
        } catch (Exception e) {
            //log strategy
            throw new UnavailableIAServiceException();
        }
    }

    @Override
    public GenerateFeedbackResponse execute(GenerateFeedbackRequest request) {
        try {
            String prompt = this.geminiFunctUtils.generateFeedbackPrompt(request);
            List<String> feedbackText = geminiFunctUtils.getResponse(prompt);
            feedbackText.removeIf(s -> s == null || s.isBlank());
            double score = Double.parseDouble(feedbackText.getLast().split("/")[0]);
            return new GenerateFeedbackResponse(feedbackText.getFirst(), score);
        } catch (Exception e) {
            //log strategy
            throw new UnavailableIAServiceException();
        }
    }
}
