package in.beze.pilot.domain;

import static in.beze.pilot.domain.ProjectTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SprintTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sprint.class);
        Sprint sprint1 = getSprintSample1();
        Sprint sprint2 = new Sprint();
        assertThat(sprint1).isNotEqualTo(sprint2);

        sprint2.setId(sprint1.getId());
        assertThat(sprint1).isEqualTo(sprint2);

        sprint2 = getSprintSample2();
        assertThat(sprint1).isNotEqualTo(sprint2);
    }

    @Test
    void projectTest() {
        Sprint sprint = getSprintRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        sprint.setProject(projectBack);
        assertThat(sprint.getProject()).isEqualTo(projectBack);

        sprint.project(null);
        assertThat(sprint.getProject()).isNull();
    }

    @Test
    void statusTest() {
        Sprint sprint = getSprintRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        sprint.setStatus(statusBack);
        assertThat(sprint.getStatus()).isEqualTo(statusBack);

        sprint.status(null);
        assertThat(sprint.getStatus()).isNull();
    }
}
