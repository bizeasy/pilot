package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RetrospectiveCriteriaTest {

    @Test
    void newRetrospectiveCriteriaHasAllFiltersNullTest() {
        var retrospectiveCriteria = new RetrospectiveCriteria();
        assertThat(retrospectiveCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void retrospectiveCriteriaFluentMethodsCreatesFiltersTest() {
        var retrospectiveCriteria = new RetrospectiveCriteria();

        setAllFilters(retrospectiveCriteria);

        assertThat(retrospectiveCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void retrospectiveCriteriaCopyCreatesNullFilterTest() {
        var retrospectiveCriteria = new RetrospectiveCriteria();
        var copy = retrospectiveCriteria.copy();

        assertThat(retrospectiveCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(retrospectiveCriteria)
        );
    }

    @Test
    void retrospectiveCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var retrospectiveCriteria = new RetrospectiveCriteria();
        setAllFilters(retrospectiveCriteria);

        var copy = retrospectiveCriteria.copy();

        assertThat(retrospectiveCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(retrospectiveCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var retrospectiveCriteria = new RetrospectiveCriteria();

        assertThat(retrospectiveCriteria).hasToString("RetrospectiveCriteria{}");
    }

    private static void setAllFilters(RetrospectiveCriteria retrospectiveCriteria) {
        retrospectiveCriteria.id();
        retrospectiveCriteria.summary();
        retrospectiveCriteria.actionItems();
        retrospectiveCriteria.dateCreated();
        retrospectiveCriteria.sprintId();
        retrospectiveCriteria.distinct();
    }

    private static Condition<RetrospectiveCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSummary()) &&
                condition.apply(criteria.getActionItems()) &&
                condition.apply(criteria.getDateCreated()) &&
                condition.apply(criteria.getSprintId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RetrospectiveCriteria> copyFiltersAre(
        RetrospectiveCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSummary(), copy.getSummary()) &&
                condition.apply(criteria.getActionItems(), copy.getActionItems()) &&
                condition.apply(criteria.getDateCreated(), copy.getDateCreated()) &&
                condition.apply(criteria.getSprintId(), copy.getSprintId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
