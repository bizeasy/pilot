package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TaskLogCriteriaTest {

    @Test
    void newTaskLogCriteriaHasAllFiltersNullTest() {
        var taskLogCriteria = new TaskLogCriteria();
        assertThat(taskLogCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void taskLogCriteriaFluentMethodsCreatesFiltersTest() {
        var taskLogCriteria = new TaskLogCriteria();

        setAllFilters(taskLogCriteria);

        assertThat(taskLogCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void taskLogCriteriaCopyCreatesNullFilterTest() {
        var taskLogCriteria = new TaskLogCriteria();
        var copy = taskLogCriteria.copy();

        assertThat(taskLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(taskLogCriteria)
        );
    }

    @Test
    void taskLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var taskLogCriteria = new TaskLogCriteria();
        setAllFilters(taskLogCriteria);

        var copy = taskLogCriteria.copy();

        assertThat(taskLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(taskLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var taskLogCriteria = new TaskLogCriteria();

        assertThat(taskLogCriteria).hasToString("TaskLogCriteria{}");
    }

    private static void setAllFilters(TaskLogCriteria taskLogCriteria) {
        taskLogCriteria.id();
        taskLogCriteria.comments();
        taskLogCriteria.fromTime();
        taskLogCriteria.toTime();
        taskLogCriteria.taskId();
        taskLogCriteria.distinct();
    }

    private static Condition<TaskLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getComments()) &&
                condition.apply(criteria.getFromTime()) &&
                condition.apply(criteria.getToTime()) &&
                condition.apply(criteria.getTaskId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TaskLogCriteria> copyFiltersAre(TaskLogCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getComments(), copy.getComments()) &&
                condition.apply(criteria.getFromTime(), copy.getFromTime()) &&
                condition.apply(criteria.getToTime(), copy.getToTime()) &&
                condition.apply(criteria.getTaskId(), copy.getTaskId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
