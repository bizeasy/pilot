package in.beze.pilot.domain;

import static in.beze.pilot.domain.AttachmentTestSamples.*;
import static in.beze.pilot.domain.PartyAttachmentTestSamples.*;
import static in.beze.pilot.domain.PartyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartyAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartyAttachment.class);
        PartyAttachment partyAttachment1 = getPartyAttachmentSample1();
        PartyAttachment partyAttachment2 = new PartyAttachment();
        assertThat(partyAttachment1).isNotEqualTo(partyAttachment2);

        partyAttachment2.setId(partyAttachment1.getId());
        assertThat(partyAttachment1).isEqualTo(partyAttachment2);

        partyAttachment2 = getPartyAttachmentSample2();
        assertThat(partyAttachment1).isNotEqualTo(partyAttachment2);
    }

    @Test
    void partyTest() {
        PartyAttachment partyAttachment = getPartyAttachmentRandomSampleGenerator();
        Party partyBack = getPartyRandomSampleGenerator();

        partyAttachment.setParty(partyBack);
        assertThat(partyAttachment.getParty()).isEqualTo(partyBack);

        partyAttachment.party(null);
        assertThat(partyAttachment.getParty()).isNull();
    }

    @Test
    void attachmentTest() {
        PartyAttachment partyAttachment = getPartyAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        partyAttachment.setAttachment(attachmentBack);
        assertThat(partyAttachment.getAttachment()).isEqualTo(attachmentBack);

        partyAttachment.attachment(null);
        assertThat(partyAttachment.getAttachment()).isNull();
    }
}
