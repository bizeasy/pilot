package in.beze.pilot.domain;

import static in.beze.pilot.domain.PartyTestSamples.*;
import static in.beze.pilot.domain.SprintTaskTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static in.beze.pilot.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SprintTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SprintTask.class);
        SprintTask sprintTask1 = getSprintTaskSample1();
        SprintTask sprintTask2 = new SprintTask();
        assertThat(sprintTask1).isNotEqualTo(sprintTask2);

        sprintTask2.setId(sprintTask1.getId());
        assertThat(sprintTask1).isEqualTo(sprintTask2);

        sprintTask2 = getSprintTaskSample2();
        assertThat(sprintTask1).isNotEqualTo(sprintTask2);
    }

    @Test
    void taskTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        sprintTask.setTask(taskBack);
        assertThat(sprintTask.getTask()).isEqualTo(taskBack);

        sprintTask.task(null);
        assertThat(sprintTask.getTask()).isNull();
    }

    @Test
    void sprintTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        sprintTask.setSprint(sprintBack);
        assertThat(sprintTask.getSprint()).isEqualTo(sprintBack);

        sprintTask.sprint(null);
        assertThat(sprintTask.getSprint()).isNull();
    }

    @Test
    void assignedToTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        sprintTask.setAssignedTo(partyBack);
        assertThat(sprintTask.getAssignedTo()).isEqualTo(partyBack);

        sprintTask.assignedTo(null);
        assertThat(sprintTask.getAssignedTo()).isNull();
    }

    @Test
    void assignedByTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        sprintTask.setAssignedBy(partyBack);
        assertThat(sprintTask.getAssignedBy()).isEqualTo(partyBack);

        sprintTask.assignedBy(null);
        assertThat(sprintTask.getAssignedBy()).isNull();
    }

    @Test
    void qaTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        sprintTask.setQa(partyBack);
        assertThat(sprintTask.getQa()).isEqualTo(partyBack);

        sprintTask.qa(null);
        assertThat(sprintTask.getQa()).isNull();
    }

    @Test
    void reviewedByTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        sprintTask.setReviewedBy(partyBack);
        assertThat(sprintTask.getReviewedBy()).isEqualTo(partyBack);

        sprintTask.reviewedBy(null);
        assertThat(sprintTask.getReviewedBy()).isNull();
    }

    @Test
    void statusTest() {
        SprintTask sprintTask = getSprintTaskRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        sprintTask.setStatus(statusBack);
        assertThat(sprintTask.getStatus()).isEqualTo(statusBack);

        sprintTask.status(null);
        assertThat(sprintTask.getStatus()).isNull();
    }
}
