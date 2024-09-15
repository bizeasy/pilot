package in.beze.pilot.domain;

import static in.beze.pilot.domain.StatusCategoryTestSamples.*;
import static in.beze.pilot.domain.StatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Status.class);
        Status status1 = getStatusSample1();
        Status status2 = new Status();
        assertThat(status1).isNotEqualTo(status2);

        status2.setId(status1.getId());
        assertThat(status1).isEqualTo(status2);

        status2 = getStatusSample2();
        assertThat(status1).isNotEqualTo(status2);
    }

    @Test
    void categoryTest() {
        Status status = getStatusRandomSampleGenerator();
        StatusCategory statusCategoryBack = getStatusCategoryRandomSampleGenerator();

        status.setCategory(statusCategoryBack);
        assertThat(status.getCategory()).isEqualTo(statusCategoryBack);

        status.category(null);
        assertThat(status.getCategory()).isNull();
    }
}
