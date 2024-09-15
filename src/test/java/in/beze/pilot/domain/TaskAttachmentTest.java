package in.beze.pilot.domain;

import static in.beze.pilot.domain.AttachmentTestSamples.*;
import static in.beze.pilot.domain.TaskAttachmentTestSamples.*;
import static in.beze.pilot.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAttachment.class);
        TaskAttachment taskAttachment1 = getTaskAttachmentSample1();
        TaskAttachment taskAttachment2 = new TaskAttachment();
        assertThat(taskAttachment1).isNotEqualTo(taskAttachment2);

        taskAttachment2.setId(taskAttachment1.getId());
        assertThat(taskAttachment1).isEqualTo(taskAttachment2);

        taskAttachment2 = getTaskAttachmentSample2();
        assertThat(taskAttachment1).isNotEqualTo(taskAttachment2);
    }

    @Test
    void taskTest() {
        TaskAttachment taskAttachment = getTaskAttachmentRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        taskAttachment.setTask(taskBack);
        assertThat(taskAttachment.getTask()).isEqualTo(taskBack);

        taskAttachment.task(null);
        assertThat(taskAttachment.getTask()).isNull();
    }

    @Test
    void attachmentTest() {
        TaskAttachment taskAttachment = getTaskAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        taskAttachment.setAttachment(attachmentBack);
        assertThat(taskAttachment.getAttachment()).isEqualTo(attachmentBack);

        taskAttachment.attachment(null);
        assertThat(taskAttachment.getAttachment()).isNull();
    }
}
