package com.studyhub.study_chat;

import com.studyhub.study_chat.event.event.Topic;
import com.studyhub.study_chat.event.event.board.BoardEvent;
import com.studyhub.study_chat.event.event.board.BoardEvent.BoardEventData;
import com.studyhub.study_chat.event.event.board.BoardEventType;
import com.studyhub.study_chat.event.event.study.StudyEvent;
import com.studyhub.study_chat.event.event.study.StudyEvent.StudyEventData;
import com.studyhub.study_chat.event.event.study.StudyEventType;
import com.studyhub.study_chat.event.event.studyMember.StudyMemberEvent;
import com.studyhub.study_chat.event.event.studyMember.StudyMemberEvent.StudyMemberEventData;
import com.studyhub.study_chat.event.event.studyMember.StudyMemberEventType;
import com.studyhub.study_chat.event.event.studyMember.StudyRole;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

@Disabled
@SpringBootTest
public class kafkaMessageMaker {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @Disabled
    public void studyCreated() {
        kafkaTemplate.send(Topic.STUDY, new StudyEvent(StudyEventType.STUDY_CREATED, new StudyEventData(2L, 1L), LocalDateTime.now()));
    }

    @Test
    @Disabled
    public void studyRemoved() {
        kafkaTemplate.send(Topic.STUDY, new StudyEvent(StudyEventType.STUDY_DELETED, new StudyEventData(2L, null), LocalDateTime.now()));
    }

    @Test
    @Disabled
    public void studyCrewJoined() {
        kafkaTemplate.send(Topic.STUDY_MEMBER, new StudyMemberEvent(StudyMemberEventType.STUDY_CREW_JOINED, new StudyMemberEventData(1L, 1L, "홍길동", StudyRole.MENTEE), LocalDateTime.now()));
    }

    @Test
    @Disabled
    public void boardCreated() {
        kafkaTemplate.send(Topic.BOARD, new BoardEvent(BoardEventType.BOARD_CREATED, new BoardEventData(1L, 1L, 1L), LocalDateTime.now()));
    }
}
