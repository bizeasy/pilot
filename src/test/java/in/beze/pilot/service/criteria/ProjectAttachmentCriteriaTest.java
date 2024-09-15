package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProjectAttachmentCriteriaTest {

    @Test
    void newProjectAttachmentCriteriaHasAllFiltersNullTest() {
        var projectAttachmentCriteria = new ProjectAttachmentCriteria();
        assertThat(projectAttachmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void projectAttachmentCriteriaFluentMethodsCreatesFiltersTest() {
        var projectAttachmentCriteria = new ProjectAttachmentCriteria();

        setAllFilters(projectAttachmentCriteria);

        assertThat(projectAttachmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void projectAttachmentCriteriaCopyCreatesNullFilterTest() {
        var projectAttachmentCriteria = new ProjectAttachmentCriteria();
        var copy = projectAttachmentCriteria.copy();

        assertThat(projectAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(projectAttachmentCriteria)
        );
    }

    @Test
    void projectAttachmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var projectAttachmentCriteria = new ProjectAttachmentCriteria();
        setAllFilters(projectAttachmentCriteria);

        var copy = projectAttachmentCriteria.copy();

        assertThat(projectAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(projectAttachmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var projectAttachmentCriteria = new ProjectAttachmentCriteria();

        assertThat(projectAttachmentCriteria).hasToString("ProjectAttachmentCriteria{}");
    }

    private static void setAllFilters(ProjectAttachmentCriteria projectAttachmentCriteria) {
        projectAttachmentCriteria.id();
        projectAttachmentCriteria.facilityId();
        projectAttachmentCriteria.attachmentId();
        projectAttachmentCriteria.distinct();
    }

    private static Condition<ProjectAttachmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFacilityId()) &&
                condition.apply(criteria.getAttachmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProjectAttachmentCriteria> copyFiltersAre(
        ProjectAttachmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFacilityId(), copy.getFacilityId()) &&
                condition.apply(criteria.getAttachmentId(), copy.getAttachmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
