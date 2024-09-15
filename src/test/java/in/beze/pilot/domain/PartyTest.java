package in.beze.pilot.domain;

import static in.beze.pilot.domain.PartyTestSamples.*;
import static in.beze.pilot.domain.PartyTypeTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Party.class);
        Party party1 = getPartySample1();
        Party party2 = new Party();
        assertThat(party1).isNotEqualTo(party2);

        party2.setId(party1.getId());
        assertThat(party1).isEqualTo(party2);

        party2 = getPartySample2();
        assertThat(party1).isNotEqualTo(party2);
    }

    @Test
    void statusTest() {
        Party party = getPartyRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        party.setStatus(statusBack);
        assertThat(party.getStatus()).isEqualTo(statusBack);

        party.status(null);
        assertThat(party.getStatus()).isNull();
    }

    @Test
    void partyTypeTest() {
        Party party = getPartyRandomSampleGenerator();
        PartyType partyTypeBack = getPartyTypeRandomSampleGenerator();

        party.setPartyType(partyTypeBack);
        assertThat(party.getPartyType()).isEqualTo(partyTypeBack);

        party.partyType(null);
        assertThat(party.getPartyType()).isNull();
    }
}
