package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CommentCriteriaTest {

    @Test
    void newCommentCriteriaHasAllFiltersNullTest() {
        var commentCriteria = new CommentCriteria();
        assertThat(commentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void commentCriteriaFluentMethodsCreatesFiltersTest() {
        var commentCriteria = new CommentCriteria();

        setAllFilters(commentCriteria);

        assertThat(commentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void commentCriteriaCopyCreatesNullFilterTest() {
        var commentCriteria = new CommentCriteria();
        var copy = commentCriteria.copy();

        assertThat(commentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(commentCriteria)
        );
    }

    @Test
    void commentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var commentCriteria = new CommentCriteria();
        setAllFilters(commentCriteria);

        var copy = commentCriteria.copy();

        assertThat(commentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(commentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var commentCriteria = new CommentCriteria();

        assertThat(commentCriteria).hasToString("CommentCriteria{}");
    }

    private static void setAllFilters(CommentCriteria commentCriteria) {
        commentCriteria.id();
        commentCriteria.text();
        commentCriteria.taskId();
        commentCriteria.distinct();
    }

    private static Condition<CommentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getText()) &&
                condition.apply(criteria.getTaskId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CommentCriteria> copyFiltersAre(CommentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getText(), copy.getText()) &&
                condition.apply(criteria.getTaskId(), copy.getTaskId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
