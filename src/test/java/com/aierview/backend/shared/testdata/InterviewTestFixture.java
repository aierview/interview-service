package com.aierview.backend.shared.testdata;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.auth.infra.persistence.entity.UserJpaEntity;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import com.aierview.backend.interview.domain.entity.Question;
import com.aierview.backend.interview.domain.enums.InterviewLevel;
import com.aierview.backend.interview.domain.enums.InterviewRole;
import com.aierview.backend.interview.domain.enums.InterviewStatus;
import com.aierview.backend.interview.domain.model.*;
import com.aierview.backend.interview.infra.persistence.entity.InterviewJpaEntity;
import com.aierview.backend.interview.infra.persistence.entity.QuestionJpaEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterviewTestFixture {
    public static BeginInterviewRequest anyBeginInterviewRequest() {
        return BeginInterviewRequest
                .builder()
                .interviewLevel(InterviewLevel.MIDLEVEL)
                .role(InterviewRole.FULLSTACK)
                .stack("any stack")
                .build();
    }

    public static Interview anyInterviewWithNoQuestions(UserRef savedUser) {

        return Interview
                .builder()
                .stack("any stack")
                .user(savedUser)
                .role(InterviewRole.FULLSTACK)
                .level(InterviewLevel.MIDLEVEL)
                .status(InterviewStatus.CREATED)
//                .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
    }

    public static InterviewJpaEntity anyInterviewJpaEntityWithNoQuestions(Interview interview) {
        UserJpaEntity userJpaEntity = AuthTestFixture.anyUserJpaEntity(interview.getUser());
        return InterviewJpaEntity
                .builder()
                .stack("any stack")
                .user(userJpaEntity)
                .role(InterviewRole.FULLSTACK)
                .level(InterviewLevel.MIDLEVEL)
                .status(InterviewStatus.CREATED)
                .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
    }

    public static InterviewJpaEntity anySavedInterviewJpaEntityWithNoQuestions(InterviewJpaEntity toSaveEntity) {
        toSaveEntity.setId(1L);
        return toSaveEntity;
    }

    public static Interview anySavedInterviewWithNoQuestions(InterviewJpaEntity savedInterviewJpaEntity) {
        UserRef userRef = AuthTestFixture.anySavedUser(savedInterviewJpaEntity.getUser());
        return Interview
                .builder()
                .id(savedInterviewJpaEntity.getId())
                .stack(savedInterviewJpaEntity.getStack())
                .user(userRef)
                .role(savedInterviewJpaEntity.getRole())
                .level(savedInterviewJpaEntity.getLevel())
                .status(savedInterviewJpaEntity.getStatus())
//                .createdAt(savedInterviewJpaEntity.getCreatedAt())
                .build();
    }

    public static Interview anySavedInterviewWithNoQuestions(Interview anyInterviewWithNoQuestions) {
        anyInterviewWithNoQuestions.setId(1L);
        return anyInterviewWithNoQuestions;
    }

    public static Question anyQuestion(Interview anySavedInterview) {
        return Question.builder().interview(anySavedInterview).question("any_question").build();
    }

    public static Question anyQuestion() {
        return Question.builder().question("any_question").build();
    }


    public static Question anySavedQuestion(Interview anySavedInterview) {
        return Question.builder().id(1L).interview(anySavedInterview).question("any_question").build();
    }

    public static List<Question> anyQuestionList(Interview anySavedInterview) {
        return List.of(anyQuestion(anySavedInterview), anyQuestion(anySavedInterview));
    }

    public static List<Question> anyQuestionList() {
        return List.of(anyQuestion(), anyQuestion());
    }

    public static List<QuestionJpaEntity> anyQuestionJpaList(List<Question> questions) {
        return questions.stream()
                .map(q -> QuestionJpaEntity.builder().question(q.getQuestion()).build())
                .toList();
    }

    public static List<QuestionJpaEntity> anySavedQuestionJpaList(List<Question> questions) {
        return questions.stream()
                .map(q -> QuestionJpaEntity.builder().id(1L).question(q.getQuestion()).build())
                .toList();
    }

    public static List<Question> anySavedQuestionList(List<QuestionJpaEntity> questions) {
        return questions.stream()
                .map(q -> Question.builder().id(q.getId()).question(q.getQuestion()).build())
                .toList();
    }


    public static List<String> anyQuestionsStringList() {
        return List.of("any_question", "any_question_2", "");
    }

    public static List<Question> anySavedQuestionList(Interview anySavedInterview) {
        Question question = anyQuestion(anySavedInterview);
        question.setId(1L);
        Question question1 = anySavedQuestion(anySavedInterview);
        question1.setId(2L);
        return List.of(question, question1);
    }

    public static Interview anySavedStartedInterviewWithQuestions(Interview anyInterviewWithNoQuestions, List<Question> questions) {
        anyInterviewWithNoQuestions.setStatus(InterviewStatus.STARTED);
        anyInterviewWithNoQuestions.setQuestions(questions);
        return anyInterviewWithNoQuestions;
    }

    public static String generateQuestionsPrompt(BeginInterviewRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("📋 Contexto:\n");
        prompt.append("Você está atuando como entrevistador e irá conduzir uma entrevista técnica com um desenvolvedor ");
        prompt.append(request.getRole() + " de nível " + request.getInterviewLevel() + " em " + request.getStack() + ".\n\n");
        prompt.append("🎯 Objetivo:\n");
        prompt.append("Gerar 3 perguntas técnicas para que o entrevistado possa responder com base em seus conhecimentos e experiência.\n\n");
        prompt.append("📝 Instrução:\n");
        prompt.append("Liste as perguntas no seguinte formato:\n\n");
        prompt.append("pergunta\n");
        prompt.append("Uma pergunta por linha.\n");
        prompt.append("Adicione ## ao final de cada pergunta para indicar o término da pergunta.\n");
        return prompt.toString();
    }

    public static String fakeResponse() {
        Map<String, Object> fakePart = new HashMap<>();
        fakePart.put("text", "Primeira linha ## Segunda linha");

        Map<String, Object> fakeContent = new HashMap<>();
        fakeContent.put("parts", List.of(fakePart));

        Map<String, Object> fakeCandidate = new HashMap<>();
        fakeCandidate.put("content", fakeContent);

        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("candidates", List.of(fakeCandidate));

        try {
            return new org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(fakeResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static CurrentQuestion anyCurrentQuestion() {
        return new CurrentQuestion(1L, "any_question", "any_audio_url");
    }


    public static InterviewEventConsumerPayload anyInterviewEventConsumerPayload() {
        return new InterviewEventConsumerPayload(1L, "any_audio_url");
    }


    public static CurrentQuestion anyCurrentQuestion(Question question) {
        return new CurrentQuestion(question.getId(), question.getQuestion(), question.getAudioUrl());
    }

    public static Question anySavedQuestion(Question question, String audioUrl) {
        question.setAudioUrl(audioUrl);
        return question;
    }

    public static Question anySavedQuestion(Question question) {
        question.setAudioUrl("any_audio_url");
        return question;
    }

    public static InterviewState anySavedInterviewState(Interview interview, Question question) {
        return new InterviewState(interview.getId(), List.of(question));
    }

    public static InterviewState anySavedInterviewState(InterviewState interviewState, List<Question> questions) {
        interviewState.setQuestions(questions);
        interviewState.setCurrentQuestionIndex(1);
        interviewState.setStatus(questions.getLast().getId(), "READY_FOR_SEND", questions.getLast());
        return interviewState;
    }

    public static InterviewState anySavedInterviewState(Long interviewId, List<Question> questions) {
        InterviewState interviewState = new InterviewState(interviewId, questions);
        interviewState.setStatus(questions.getLast().getId(), "READY_FOR_SEND", questions.getLast());
        return interviewState;
    }

    public static QuestionReceivedRequest anyOnQuestionReceivedRequest() {
        return new QuestionReceivedRequest(1L);
    }

    public static AnswerQuestionRequest anyOnQuestionOnAnswerReceivedRequest() {
        return new AnswerQuestionRequest(1L, "any_answer");
    }
}
