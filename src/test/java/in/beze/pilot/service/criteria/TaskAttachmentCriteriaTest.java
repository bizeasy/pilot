package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TaskAttachmentCriteriaTest {

    @Test
    void newTaskAttachmentCriteriaHasAllFiltersNullTest() {
        var taskAttachmentCriteria = new TaskAttachmentCriteria();
        assertThat(taskAttachmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void taskAttachmentCriteriaFluentMethodsCreatesFiltersTest() {
        var taskAttachmentCriteria = new TaskAttachmentCriteria();

        setAllFilters(taskAttachmentCriteria);

        assertThat(taskAttachmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void taskAttachmentCriteriaCopyCreatesNullFilterTest() {
        var taskAttachmentCriteria = new TaskAttachmentCriteria();
        var copy = taskAttachmentCriteria.copy();

        assertThat(taskAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(taskAttachmentCriteria)
        );
    }

    @Test
    void taskAttachmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var taskAttachmentCriteria = new TaskAttachmentCriteria();
        setAllFilters(taskAttachmentCriteria);

        var copy = taskAttachmentCriteria.copy();

        assertThat(taskAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(taskAttachmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var taskAttachmentCriteria = new TaskAttachmentCriteria();

        assertThat(taskAttachmentCriteria).hasToString("TaskAttachmentCriteria{}");
    }

    private static void setAllFilters(TaskAttachmentCriteria taskAttachmentCriteria) {
        taskAttachmentCriteria.id();
        taskAttachmentCriteria.taskId();
        taskAttachmentCriteria.attachmentId();
        taskAttachmentCriteria.distinct();
    }

    private static Condition<TaskAttachmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTaskId()) &&
                condition.apply(criteria.getAttachmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TaskAttachmentCriteria> copyFiltersAre(
        TaskAttachmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTaskId(), copy.getTaskId()) &&
                condition.apply(criteria.getAttachmentId(), copy.getAttachmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
