package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TaskCriteriaTest {

    @Test
    void newTaskCriteriaHasAllFiltersNullTest() {
        var taskCriteria = new TaskCriteria();
        assertThat(taskCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void taskCriteriaFluentMethodsCreatesFiltersTest() {
        var taskCriteria = new TaskCriteria();

        setAllFilters(taskCriteria);

        assertThat(taskCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void taskCriteriaCopyCreatesNullFilterTest() {
        var taskCriteria = new TaskCriteria();
        var copy = taskCriteria.copy();

        assertThat(taskCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(taskCriteria)
        );
    }

    @Test
    void taskCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var taskCriteria = new TaskCriteria();
        setAllFilters(taskCriteria);

        var copy = taskCriteria.copy();

        assertThat(taskCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(taskCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var taskCriteria = new TaskCriteria();

        assertThat(taskCriteria).hasToString("TaskCriteria{}");
    }

    private static void setAllFilters(TaskCriteria taskCriteria) {
        taskCriteria.id();
        taskCriteria.title();
        taskCriteria.description();
        taskCriteria.priority();
        taskCriteria.dueDate();
        taskCriteria.storyPoints();
        taskCriteria.startTime();
        taskCriteria.pauseTime();
        taskCriteria.endTime();
        taskCriteria.totalDuration();
        taskCriteria.sequenceNo();
        taskCriteria.commentsId();
        taskCriteria.projectId();
        taskCriteria.statusId();
        taskCriteria.assigneeId();
        taskCriteria.distinct();
    }

    private static Condition<TaskCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getPriority()) &&
                condition.apply(criteria.getDueDate()) &&
                condition.apply(criteria.getStoryPoints()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getPauseTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getTotalDuration()) &&
                condition.apply(criteria.getSequenceNo()) &&
                condition.apply(criteria.getCommentsId()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getAssigneeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TaskCriteria> copyFiltersAre(TaskCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getPriority(), copy.getPriority()) &&
                condition.apply(criteria.getDueDate(), copy.getDueDate()) &&
                condition.apply(criteria.getStoryPoints(), copy.getStoryPoints()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getPauseTime(), copy.getPauseTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getTotalDuration(), copy.getTotalDuration()) &&
                condition.apply(criteria.getSequenceNo(), copy.getSequenceNo()) &&
                condition.apply(criteria.getCommentsId(), copy.getCommentsId()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getAssigneeId(), copy.getAssigneeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
