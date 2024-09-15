package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SprintCriteriaTest {

    @Test
    void newSprintCriteriaHasAllFiltersNullTest() {
        var sprintCriteria = new SprintCriteria();
        assertThat(sprintCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void sprintCriteriaFluentMethodsCreatesFiltersTest() {
        var sprintCriteria = new SprintCriteria();

        setAllFilters(sprintCriteria);

        assertThat(sprintCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void sprintCriteriaCopyCreatesNullFilterTest() {
        var sprintCriteria = new SprintCriteria();
        var copy = sprintCriteria.copy();

        assertThat(sprintCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(sprintCriteria)
        );
    }

    @Test
    void sprintCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sprintCriteria = new SprintCriteria();
        setAllFilters(sprintCriteria);

        var copy = sprintCriteria.copy();

        assertThat(sprintCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(sprintCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sprintCriteria = new SprintCriteria();

        assertThat(sprintCriteria).hasToString("SprintCriteria{}");
    }

    private static void setAllFilters(SprintCriteria sprintCriteria) {
        sprintCriteria.id();
        sprintCriteria.name();
        sprintCriteria.startDate();
        sprintCriteria.endDate();
        sprintCriteria.goal();
        sprintCriteria.totalPoints();
        sprintCriteria.projectId();
        sprintCriteria.statusId();
        sprintCriteria.distinct();
    }

    private static Condition<SprintCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getGoal()) &&
                condition.apply(criteria.getTotalPoints()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SprintCriteria> copyFiltersAre(SprintCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getGoal(), copy.getGoal()) &&
                condition.apply(criteria.getTotalPoints(), copy.getTotalPoints()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
