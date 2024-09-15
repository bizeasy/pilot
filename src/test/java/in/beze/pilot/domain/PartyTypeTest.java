package in.beze.pilot.domain;

import static in.beze.pilot.domain.PartyTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartyTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartyType.class);
        PartyType partyType1 = getPartyTypeSample1();
        PartyType partyType2 = new PartyType();
        assertThat(partyType1).isNotEqualTo(partyType2);

        partyType2.setId(partyType1.getId());
        assertThat(partyType1).isEqualTo(partyType2);

        partyType2 = getPartyTypeSample2();
        assertThat(partyType1).isNotEqualTo(partyType2);
    }
}
