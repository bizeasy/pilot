package in.beze.pilot.domain;

import static in.beze.pilot.domain.TaskLogTestSamples.*;
import static in.beze.pilot.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskLog.class);
        TaskLog taskLog1 = getTaskLogSample1();
        TaskLog taskLog2 = new TaskLog();
        assertThat(taskLog1).isNotEqualTo(taskLog2);

        taskLog2.setId(taskLog1.getId());
        assertThat(taskLog1).isEqualTo(taskLog2);

        taskLog2 = getTaskLogSample2();
        assertThat(taskLog1).isNotEqualTo(taskLog2);
    }

    @Test
    void taskTest() {
        TaskLog taskLog = getTaskLogRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        taskLog.setTask(taskBack);
        assertThat(taskLog.getTask()).isEqualTo(taskBack);

        taskLog.task(null);
        assertThat(taskLog.getTask()).isNull();
    }
}
