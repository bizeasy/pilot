package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SprintTaskCriteriaTest {

    @Test
    void newSprintTaskCriteriaHasAllFiltersNullTest() {
        var sprintTaskCriteria = new SprintTaskCriteria();
        assertThat(sprintTaskCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void sprintTaskCriteriaFluentMethodsCreatesFiltersTest() {
        var sprintTaskCriteria = new SprintTaskCriteria();

        setAllFilters(sprintTaskCriteria);

        assertThat(sprintTaskCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void sprintTaskCriteriaCopyCreatesNullFilterTest() {
        var sprintTaskCriteria = new SprintTaskCriteria();
        var copy = sprintTaskCriteria.copy();

        assertThat(sprintTaskCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(sprintTaskCriteria)
        );
    }

    @Test
    void sprintTaskCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sprintTaskCriteria = new SprintTaskCriteria();
        setAllFilters(sprintTaskCriteria);

        var copy = sprintTaskCriteria.copy();

        assertThat(sprintTaskCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(sprintTaskCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sprintTaskCriteria = new SprintTaskCriteria();

        assertThat(sprintTaskCriteria).hasToString("SprintTaskCriteria{}");
    }

    private static void setAllFilters(SprintTaskCriteria sprintTaskCriteria) {
        sprintTaskCriteria.id();
        sprintTaskCriteria.sequenceNo();
        sprintTaskCriteria.storyPoints();
        sprintTaskCriteria.fromTime();
        sprintTaskCriteria.thruTime();
        sprintTaskCriteria.assignedTime();
        sprintTaskCriteria.duration();
        sprintTaskCriteria.taskId();
        sprintTaskCriteria.sprintId();
        sprintTaskCriteria.assignedToId();
        sprintTaskCriteria.assignedById();
        sprintTaskCriteria.qaId();
        sprintTaskCriteria.reviewedById();
        sprintTaskCriteria.statusId();
        sprintTaskCriteria.distinct();
    }

    private static Condition<SprintTaskCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSequenceNo()) &&
                condition.apply(criteria.getStoryPoints()) &&
                condition.apply(criteria.getFromTime()) &&
                condition.apply(criteria.getThruTime()) &&
                condition.apply(criteria.getAssignedTime()) &&
                condition.apply(criteria.getDuration()) &&
                condition.apply(criteria.getTaskId()) &&
                condition.apply(criteria.getSprintId()) &&
                condition.apply(criteria.getAssignedToId()) &&
                condition.apply(criteria.getAssignedById()) &&
                condition.apply(criteria.getQaId()) &&
                condition.apply(criteria.getReviewedById()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SprintTaskCriteria> copyFiltersAre(SprintTaskCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSequenceNo(), copy.getSequenceNo()) &&
                condition.apply(criteria.getStoryPoints(), copy.getStoryPoints()) &&
                condition.apply(criteria.getFromTime(), copy.getFromTime()) &&
                condition.apply(criteria.getThruTime(), copy.getThruTime()) &&
                condition.apply(criteria.getAssignedTime(), copy.getAssignedTime()) &&
                condition.apply(criteria.getDuration(), copy.getDuration()) &&
                condition.apply(criteria.getTaskId(), copy.getTaskId()) &&
                condition.apply(criteria.getSprintId(), copy.getSprintId()) &&
                condition.apply(criteria.getAssignedToId(), copy.getAssignedToId()) &&
                condition.apply(criteria.getAssignedById(), copy.getAssignedById()) &&
                condition.apply(criteria.getQaId(), copy.getQaId()) &&
                condition.apply(criteria.getReviewedById(), copy.getReviewedById()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
