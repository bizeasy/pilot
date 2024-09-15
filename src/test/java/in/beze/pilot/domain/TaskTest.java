package in.beze.pilot.domain;

import static in.beze.pilot.domain.CommentTestSamples.*;
import static in.beze.pilot.domain.PartyTestSamples.*;
import static in.beze.pilot.domain.ProjectTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static in.beze.pilot.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void commentsTest() {
        Task task = getTaskRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        task.addComments(commentBack);
        assertThat(task.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getTask()).isEqualTo(task);

        task.removeComments(commentBack);
        assertThat(task.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getTask()).isNull();

        task.comments(new HashSet<>(Set.of(commentBack)));
        assertThat(task.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getTask()).isEqualTo(task);

        task.setComments(new HashSet<>());
        assertThat(task.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getTask()).isNull();
    }

    @Test
    void projectTest() {
        Task task = getTaskRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        task.setProject(projectBack);
        assertThat(task.getProject()).isEqualTo(projectBack);

        task.project(null);
        assertThat(task.getProject()).isNull();
    }

    @Test
    void statusTest() {
        Task task = getTaskRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        task.setStatus(statusBack);
        assertThat(task.getStatus()).isEqualTo(statusBack);

        task.status(null);
        assertThat(task.getStatus()).isNull();
    }

    @Test
    void assigneeTest() {
        Task task = getTaskRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        task.setAssignee(partyBack);
        assertThat(task.getAssignee()).isEqualTo(partyBack);

        task.assignee(null);
        assertThat(task.getAssignee()).isNull();
    }
}
