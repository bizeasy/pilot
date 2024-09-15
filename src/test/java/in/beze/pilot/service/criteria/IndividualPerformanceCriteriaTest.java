package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class IndividualPerformanceCriteriaTest {

    @Test
    void newIndividualPerformanceCriteriaHasAllFiltersNullTest() {
        var individualPerformanceCriteria = new IndividualPerformanceCriteria();
        assertThat(individualPerformanceCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void individualPerformanceCriteriaFluentMethodsCreatesFiltersTest() {
        var individualPerformanceCriteria = new IndividualPerformanceCriteria();

        setAllFilters(individualPerformanceCriteria);

        assertThat(individualPerformanceCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void individualPerformanceCriteriaCopyCreatesNullFilterTest() {
        var individualPerformanceCriteria = new IndividualPerformanceCriteria();
        var copy = individualPerformanceCriteria.copy();

        assertThat(individualPerformanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(individualPerformanceCriteria)
        );
    }

    @Test
    void individualPerformanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var individualPerformanceCriteria = new IndividualPerformanceCriteria();
        setAllFilters(individualPerformanceCriteria);

        var copy = individualPerformanceCriteria.copy();

        assertThat(individualPerformanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(individualPerformanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var individualPerformanceCriteria = new IndividualPerformanceCriteria();

        assertThat(individualPerformanceCriteria).hasToString("IndividualPerformanceCriteria{}");
    }

    private static void setAllFilters(IndividualPerformanceCriteria individualPerformanceCriteria) {
        individualPerformanceCriteria.id();
        individualPerformanceCriteria.completedTasks();
        individualPerformanceCriteria.velocity();
        individualPerformanceCriteria.storyPointsCompleted();
        individualPerformanceCriteria.partyId();
        individualPerformanceCriteria.sprintId();
        individualPerformanceCriteria.distinct();
    }

    private static Condition<IndividualPerformanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCompletedTasks()) &&
                condition.apply(criteria.getVelocity()) &&
                condition.apply(criteria.getStoryPointsCompleted()) &&
                condition.apply(criteria.getPartyId()) &&
                condition.apply(criteria.getSprintId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<IndividualPerformanceCriteria> copyFiltersAre(
        IndividualPerformanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCompletedTasks(), copy.getCompletedTasks()) &&
                condition.apply(criteria.getVelocity(), copy.getVelocity()) &&
                condition.apply(criteria.getStoryPointsCompleted(), copy.getStoryPointsCompleted()) &&
                condition.apply(criteria.getPartyId(), copy.getPartyId()) &&
                condition.apply(criteria.getSprintId(), copy.getSprintId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
