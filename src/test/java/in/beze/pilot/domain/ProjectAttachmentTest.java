package in.beze.pilot.domain;

import static in.beze.pilot.domain.AttachmentTestSamples.*;
import static in.beze.pilot.domain.ProjectAttachmentTestSamples.*;
import static in.beze.pilot.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectAttachment.class);
        ProjectAttachment projectAttachment1 = getProjectAttachmentSample1();
        ProjectAttachment projectAttachment2 = new ProjectAttachment();
        assertThat(projectAttachment1).isNotEqualTo(projectAttachment2);

        projectAttachment2.setId(projectAttachment1.getId());
        assertThat(projectAttachment1).isEqualTo(projectAttachment2);

        projectAttachment2 = getProjectAttachmentSample2();
        assertThat(projectAttachment1).isNotEqualTo(projectAttachment2);
    }

    @Test
    void facilityTest() {
        ProjectAttachment projectAttachment = getProjectAttachmentRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        projectAttachment.setFacility(projectBack);
        assertThat(projectAttachment.getFacility()).isEqualTo(projectBack);

        projectAttachment.facility(null);
        assertThat(projectAttachment.getFacility()).isNull();
    }

    @Test
    void attachmentTest() {
        ProjectAttachment projectAttachment = getProjectAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        projectAttachment.setAttachment(attachmentBack);
        assertThat(projectAttachment.getAttachment()).isEqualTo(attachmentBack);

        projectAttachment.attachment(null);
        assertThat(projectAttachment.getAttachment()).isNull();
    }
}
