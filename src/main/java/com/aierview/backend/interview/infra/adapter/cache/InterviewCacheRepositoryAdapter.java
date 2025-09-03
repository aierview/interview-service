package com.aierview.backend.interview.infra.adapter.cache;

import com.aierview.backend.interview.domain.contract.cache.IInterviewCacheRepository;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.domain.entity.InterviewState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterviewCacheRepositoryAdapter implements IInterviewCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(Interview interview) {
        InterviewState state = new InterviewState(interview.getId(), interview.getQuestions());
        this.redisTemplate.opsForValue().set("interview:" + interview.getId(), state);
    }

    @Override
    public InterviewState get(Long interviewId) {
        return (InterviewState) redisTemplate.opsForValue().get("interview:" + interviewId);
    }

    @Override
    public void remove(Long interviewId) {
        redisTemplate.delete("interview:" + interviewId);
    }

    @Override
    public void revalidate(Long interviewId, InterviewState newState) {
        InterviewState existing = (InterviewState) redisTemplate.opsForValue().get("interview:" + interviewId);
        if (existing != null) {
            existing.setCurrentQuestionIndex(newState.getCurrentQuestionIndex());
            existing.setQuestionStatus(newState.getQuestionStatus());
            existing.setQuestions(newState.getQuestions());
            redisTemplate.opsForValue().set("interview:" + interviewId, existing);
        } else {
            redisTemplate.opsForValue().set("interview:" + interviewId, newState);
        }
    }

}
