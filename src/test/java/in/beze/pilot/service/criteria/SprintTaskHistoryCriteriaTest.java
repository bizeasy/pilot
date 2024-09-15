package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SprintTaskHistoryCriteriaTest {

    @Test
    void newSprintTaskHistoryCriteriaHasAllFiltersNullTest() {
        var sprintTaskHistoryCriteria = new SprintTaskHistoryCriteria();
        assertThat(sprintTaskHistoryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void sprintTaskHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var sprintTaskHistoryCriteria = new SprintTaskHistoryCriteria();

        setAllFilters(sprintTaskHistoryCriteria);

        assertThat(sprintTaskHistoryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void sprintTaskHistoryCriteriaCopyCreatesNullFilterTest() {
        var sprintTaskHistoryCriteria = new SprintTaskHistoryCriteria();
        var copy = sprintTaskHistoryCriteria.copy();

        assertThat(sprintTaskHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(sprintTaskHistoryCriteria)
        );
    }

    @Test
    void sprintTaskHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sprintTaskHistoryCriteria = new SprintTaskHistoryCriteria();
        setAllFilters(sprintTaskHistoryCriteria);

        var copy = sprintTaskHistoryCriteria.copy();

        assertThat(sprintTaskHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(sprintTaskHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sprintTaskHistoryCriteria = new SprintTaskHistoryCriteria();

        assertThat(sprintTaskHistoryCriteria).hasToString("SprintTaskHistoryCriteria{}");
    }

    private static void setAllFilters(SprintTaskHistoryCriteria sprintTaskHistoryCriteria) {
        sprintTaskHistoryCriteria.id();
        sprintTaskHistoryCriteria.comments();
        sprintTaskHistoryCriteria.fromDate();
        sprintTaskHistoryCriteria.toDate();
        sprintTaskHistoryCriteria.fromStatusId();
        sprintTaskHistoryCriteria.toStatusId();
        sprintTaskHistoryCriteria.distinct();
    }

    private static Condition<SprintTaskHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getComments()) &&
                condition.apply(criteria.getFromDate()) &&
                condition.apply(criteria.getToDate()) &&
                condition.apply(criteria.getFromStatusId()) &&
                condition.apply(criteria.getToStatusId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SprintTaskHistoryCriteria> copyFiltersAre(
        SprintTaskHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getComments(), copy.getComments()) &&
                condition.apply(criteria.getFromDate(), copy.getFromDate()) &&
                condition.apply(criteria.getToDate(), copy.getToDate()) &&
                condition.apply(criteria.getFromStatusId(), copy.getFromStatusId()) &&
                condition.apply(criteria.getToStatusId(), copy.getToStatusId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
