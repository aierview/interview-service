package com.aierview.backend.interview.domain.contract.IA;

import com.aierview.backend.interview.domain.model.GenerateFeedbackRequest;
import com.aierview.backend.interview.domain.model.GenerateFeedbackResponse;

public interface IIAGenerateFeedback {
    GenerateFeedbackResponse execute(GenerateFeedbackRequest request);
}
