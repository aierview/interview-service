package com.aierview.backend.interview.domain.model;

import com.aierview.backend.interview.domain.enums.InterviewLevel;
import com.aierview.backend.interview.domain.enums.InterviewRole;

public record GenerateFeedbackRequest(String question, String answerText, InterviewRole role, InterviewLevel level,
                                      String stack) {
}