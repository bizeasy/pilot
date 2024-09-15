package in.beze.pilot.domain;

import static in.beze.pilot.domain.RetrospectiveTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RetrospectiveTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Retrospective.class);
        Retrospective retrospective1 = getRetrospectiveSample1();
        Retrospective retrospective2 = new Retrospective();
        assertThat(retrospective1).isNotEqualTo(retrospective2);

        retrospective2.setId(retrospective1.getId());
        assertThat(retrospective1).isEqualTo(retrospective2);

        retrospective2 = getRetrospectiveSample2();
        assertThat(retrospective1).isNotEqualTo(retrospective2);
    }

    @Test
    void sprintTest() {
        Retrospective retrospective = getRetrospectiveRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        retrospective.setSprint(sprintBack);
        assertThat(retrospective.getSprint()).isEqualTo(sprintBack);

        retrospective.sprint(null);
        assertThat(retrospective.getSprint()).isNull();
    }
}
