package in.beze.pilot.domain;

import static in.beze.pilot.domain.StatusCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import in.beze.pilot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatusCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusCategory.class);
        StatusCategory statusCategory1 = getStatusCategorySample1();
        StatusCategory statusCategory2 = new StatusCategory();
        assertThat(statusCategory1).isNotEqualTo(statusCategory2);

        statusCategory2.setId(statusCategory1.getId());
        assertThat(statusCategory1).isEqualTo(statusCategory2);

        statusCategory2 = getStatusCategorySample2();
        assertThat(statusCategory1).isNotEqualTo(statusCategory2);
    }
}
