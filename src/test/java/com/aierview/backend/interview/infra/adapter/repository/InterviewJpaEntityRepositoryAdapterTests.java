package com.aierview.backend.interview.infra.adapter.repository;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.interview.domain.contract.repository.IInterviewRepository;
import com.aierview.backend.interview.domain.entity.Interview;
import com.aierview.backend.interview.infra.mapper.InterviewMapper;
import com.aierview.backend.interview.infra.persistence.entity.InterviewJpaEntity;
import com.aierview.backend.interview.infra.persistence.repository.InterviewJpaRepository;
import com.aierview.backend.shared.testdata.AuthTestFixture;
import com.aierview.backend.shared.testdata.InterviewTestFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InterviewJpaEntityRepositoryAdapterTests {
    private IInterviewRepository interviewRepository;
    private InterviewMapper interviewMapper;
    private InterviewJpaRepository interviewJpaRepository;

    @BeforeEach
    void setUp() {
        this.interviewMapper = Mockito.mock(InterviewMapper.class);
        this.interviewJpaRepository = Mockito.mock(InterviewJpaRepository.class);
        this.interviewRepository = new InterviewRepositoryAdapter(interviewMapper, interviewJpaRepository);
    }

    @Test
    @DisplayName("Should save interview and return saved interview")
    void shouldSaveInterviewAndReturnSavedInterview() {
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview toSaveInterview = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);

        InterviewJpaEntity toInterviewJpaEntity = InterviewTestFixture.anyInterviewJpaEntityWithNoQuestions(toSaveInterview);
        InterviewJpaEntity savedInterviewJpaEntity = InterviewTestFixture.anySavedInterviewJpaEntityWithNoQuestions(toInterviewJpaEntity);

        Interview savedInterview = InterviewTestFixture.anySavedInterviewWithNoQuestions(savedInterviewJpaEntity);

        Mockito.when(this.interviewMapper.mapToJpa(toSaveInterview)).thenReturn(toInterviewJpaEntity);
        Mockito.when(this.interviewJpaRepository.save(toInterviewJpaEntity)).thenReturn(savedInterviewJpaEntity);
        Mockito.when(this.interviewMapper.mapToEntity(savedInterviewJpaEntity)).thenReturn(savedInterview);

        Interview result = this.interviewRepository.save(toSaveInterview);

        Assertions.assertEquals(result.getId(), savedInterviewJpaEntity.getId());
        Assertions.assertEquals(result.getStack(), savedInterview.getStack());
        Assertions.assertEquals(result.getRole(), savedInterview.getRole());
        Assertions.assertEquals(result.getLevel(), savedInterview.getLevel());
        Assertions.assertEquals(result.getStatus(), savedInterview.getStatus());
//        Assertions.assertEquals(result.getCreatedAt(), savedInterview.getCreatedAt());

        Mockito.verify(this.interviewMapper, Mockito.times(1)).mapToJpa(toSaveInterview);
        Mockito.verify(interviewJpaRepository, Mockito.times(1)).save(toInterviewJpaEntity);
        Mockito.verify(this.interviewMapper, Mockito.times(1)).mapToEntity(savedInterviewJpaEntity);
    }

    @Test
    @DisplayName("Should update interview and return")
    void shouldUpdateInterviewAndReturnUpdatedInterview() {
        UserRef savedUser = AuthTestFixture.anySavedUserRef();

        Interview toSaveInterview = InterviewTestFixture.anyInterviewWithNoQuestions(savedUser);

        InterviewJpaEntity toInterviewJpaEntity = InterviewTestFixture.anyInterviewJpaEntityWithNoQuestions(toSaveInterview);
        InterviewJpaEntity savedInterviewJpaEntity = InterviewTestFixture.anySavedInterviewJpaEntityWithNoQuestions(toInterviewJpaEntity);

        Interview savedInterview = InterviewTestFixture.anySavedInterviewWithNoQuestions(savedInterviewJpaEntity);

        Mockito.when(this.interviewMapper.mapToJpa(toSaveInterview)).thenReturn(toInterviewJpaEntity);
        Mockito.when(this.interviewJpaRepository.save(toInterviewJpaEntity)).thenReturn(savedInterviewJpaEntity);
        Mockito.when(this.interviewMapper.mapToEntity(savedInterviewJpaEntity)).thenReturn(savedInterview);

        Interview result = this.interviewRepository.update(toSaveInterview);

        Assertions.assertEquals(result.getId(), savedInterviewJpaEntity.getId());
        Assertions.assertEquals(result.getStack(), savedInterview.getStack());
        Assertions.assertEquals(result.getRole(), savedInterview.getRole());
        Assertions.assertEquals(result.getLevel(), savedInterview.getLevel());
        Assertions.assertEquals(result.getStatus(), savedInterview.getStatus());
//        Assertions.assertEquals(result.getCreatedAt(), savedInterview.getCreatedAt());

        Mockito.verify(this.interviewMapper, Mockito.times(1)).mapToJpa(toSaveInterview);
        Mockito.verify(interviewJpaRepository, Mockito.times(1)).save(toInterviewJpaEntity);
        Mockito.verify(this.interviewMapper, Mockito.times(1)).mapToEntity(savedInterviewJpaEntity);

    }
}
