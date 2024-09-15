package in.beze.pilot.domain;

import static in.beze.pilot.domain.SprintTaskHistoryTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SprintTaskHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SprintTaskHistory.class);
        SprintTaskHistory sprintTaskHistory1 = getSprintTaskHistorySample1();
        SprintTaskHistory sprintTaskHistory2 = new SprintTaskHistory();
        assertThat(sprintTaskHistory1).isNotEqualTo(sprintTaskHistory2);

        sprintTaskHistory2.setId(sprintTaskHistory1.getId());
        assertThat(sprintTaskHistory1).isEqualTo(sprintTaskHistory2);

        sprintTaskHistory2 = getSprintTaskHistorySample2();
        assertThat(sprintTaskHistory1).isNotEqualTo(sprintTaskHistory2);
    }

    @Test
    void fromStatusTest() {
        SprintTaskHistory sprintTaskHistory = getSprintTaskHistoryRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        sprintTaskHistory.setFromStatus(statusBack);
        assertThat(sprintTaskHistory.getFromStatus()).isEqualTo(statusBack);

        sprintTaskHistory.fromStatus(null);
        assertThat(sprintTaskHistory.getFromStatus()).isNull();
    }

    @Test
    void toStatusTest() {
        SprintTaskHistory sprintTaskHistory = getSprintTaskHistoryRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        sprintTaskHistory.setToStatus(statusBack);
        assertThat(sprintTaskHistory.getToStatus()).isEqualTo(statusBack);

        sprintTaskHistory.toStatus(null);
        assertThat(sprintTaskHistory.getToStatus()).isNull();
    }
}
