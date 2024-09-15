package in.beze.pilot.domain;

import static in.beze.pilot.domain.PartyTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static in.beze.pilot.domain.TaskHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskHistory.class);
        TaskHistory taskHistory1 = getTaskHistorySample1();
        TaskHistory taskHistory2 = new TaskHistory();
        assertThat(taskHistory1).isNotEqualTo(taskHistory2);

        taskHistory2.setId(taskHistory1.getId());
        assertThat(taskHistory1).isEqualTo(taskHistory2);

        taskHistory2 = getTaskHistorySample2();
        assertThat(taskHistory1).isNotEqualTo(taskHistory2);
    }

    @Test
    void assignedToTest() {
        TaskHistory taskHistory = getTaskHistoryRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        taskHistory.setAssignedTo(partyBack);
        assertThat(taskHistory.getAssignedTo()).isEqualTo(partyBack);

        taskHistory.assignedTo(null);
        assertThat(taskHistory.getAssignedTo()).isNull();
    }

    @Test
    void sprintTest() {
        TaskHistory taskHistory = getTaskHistoryRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        taskHistory.setSprint(sprintBack);
        assertThat(taskHistory.getSprint()).isEqualTo(sprintBack);

        taskHistory.sprint(null);
        assertThat(taskHistory.getSprint()).isNull();
    }

    @Test
    void assignedByTest() {
        TaskHistory taskHistory = getTaskHistoryRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        taskHistory.setAssignedBy(partyBack);
        assertThat(taskHistory.getAssignedBy()).isEqualTo(partyBack);

        taskHistory.assignedBy(null);
        assertThat(taskHistory.getAssignedBy()).isNull();
    }
}
