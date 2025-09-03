package com.aierview.backend.interview.domain.entity;

import com.aierview.backend.auth.domain.entity.UserRef;
import com.aierview.backend.interview.domain.enums.InterviewLevel;
import com.aierview.backend.interview.domain.enums.InterviewRole;
import com.aierview.backend.interview.domain.enums.InterviewStatus;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"questions"})
public class Interview {
    private Long id;
    private String stack;
    private UserRef user;
    private InterviewRole role;
    private InterviewLevel level;
    private InterviewStatus status;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime createdAt;
    private List<Question> questions;
}
