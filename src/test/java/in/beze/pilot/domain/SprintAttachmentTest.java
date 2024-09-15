package in.beze.pilot.domain;

import static in.beze.pilot.domain.AttachmentTestSamples.*;
import static in.beze.pilot.domain.SprintAttachmentTestSamples.*;
import static in.beze.pilot.domain.SprintTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SprintAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SprintAttachment.class);
        SprintAttachment sprintAttachment1 = getSprintAttachmentSample1();
        SprintAttachment sprintAttachment2 = new SprintAttachment();
        assertThat(sprintAttachment1).isNotEqualTo(sprintAttachment2);

        sprintAttachment2.setId(sprintAttachment1.getId());
        assertThat(sprintAttachment1).isEqualTo(sprintAttachment2);

        sprintAttachment2 = getSprintAttachmentSample2();
        assertThat(sprintAttachment1).isNotEqualTo(sprintAttachment2);
    }

    @Test
    void sprintTest() {
        SprintAttachment sprintAttachment = getSprintAttachmentRandomSampleGenerator();
        Sprint sprintBack = getSprintRandomSampleGenerator();

        sprintAttachment.setSprint(sprintBack);
        assertThat(sprintAttachment.getSprint()).isEqualTo(sprintBack);

        sprintAttachment.sprint(null);
        assertThat(sprintAttachment.getSprint()).isNull();
    }

    @Test
    void attachmentTest() {
        SprintAttachment sprintAttachment = getSprintAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        sprintAttachment.setAttachment(attachmentBack);
        assertThat(sprintAttachment.getAttachment()).isEqualTo(attachmentBack);

        sprintAttachment.attachment(null);
        assertThat(sprintAttachment.getAttachment()).isNull();
    }
}
