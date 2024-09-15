package in.beze.pilot.domain;

import static in.beze.pilot.domain.SprintTestSamples.*;
import static in.beze.pilot.domain.TeamVelocityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamVelocityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamVelocity.class);
        TeamVelocity teamVelocity1 = getTeamVelocitySample1();
        TeamVelocity teamVelocity2 = new TeamVelocity();
        assertThat(teamVelocity1).isNotEqualTo(teamVelocity2);

        teamVelocity2.setId(teamVelocity1.getId());
        assertThat(teamVelocity1).isEqualTo(teamVelocity2);

        teamVelocity2 = getTeamVelocitySample2();
        assertThat(teamVelocity1).isNotEqualTo(teamVelocity2);
    }

    @Test
    void sprintTest() {
        TeamVelocity teamVelocity = getTeamVelocityRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        teamVelocity.setSprint(sprintBack);
        assertThat(teamVelocity.getSprint()).isEqualTo(sprintBack);

        teamVelocity.sprint(null);
        assertThat(teamVelocity.getSprint()).isNull();
    }
}
