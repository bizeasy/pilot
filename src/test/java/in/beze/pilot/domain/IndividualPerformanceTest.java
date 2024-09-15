package in.beze.pilot.domain;

import static in.beze.pilot.domain.IndividualPerformanceTestSamples.*;
import static in.beze.pilot.domain.PartyTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IndividualPerformanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IndividualPerformance.class);
        IndividualPerformance individualPerformance1 = getIndividualPerformanceSample1();
        IndividualPerformance individualPerformance2 = new IndividualPerformance();
        assertThat(individualPerformance1).isNotEqualTo(individualPerformance2);

        individualPerformance2.setId(individualPerformance1.getId());
        assertThat(individualPerformance1).isEqualTo(individualPerformance2);

        individualPerformance2 = getIndividualPerformanceSample2();
        assertThat(individualPerformance1).isNotEqualTo(individualPerformance2);
    }

    @Test
    void partyTest() {
        IndividualPerformance individualPerformance = getIndividualPerformanceRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        individualPerformance.setParty(partyBack);
        assertThat(individualPerformance.getParty()).isEqualTo(partyBack);

        individualPerformance.party(null);
        assertThat(individualPerformance.getParty()).isNull();
    }

    @Test
    void sprintTest() {
        IndividualPerformance individualPerformance = getIndividualPerformanceRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        individualPerformance.setSprint(sprintBack);
        assertThat(individualPerformance.getSprint()).isEqualTo(sprintBack);

        individualPerformance.sprint(null);
        assertThat(individualPerformance.getSprint()).isNull();
    }
}
