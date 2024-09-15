package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StatusCriteriaTest {

    @Test
    void newStatusCriteriaHasAllFiltersNullTest() {
        var statusCriteria = new StatusCriteria();
        assertThat(statusCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void statusCriteriaFluentMethodsCreatesFiltersTest() {
        var statusCriteria = new StatusCriteria();

        setAllFilters(statusCriteria);

        assertThat(statusCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void statusCriteriaCopyCreatesNullFilterTest() {
        var statusCriteria = new StatusCriteria();
        var copy = statusCriteria.copy();

        assertThat(statusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(statusCriteria)
        );
    }

    @Test
    void statusCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var statusCriteria = new StatusCriteria();
        setAllFilters(statusCriteria);

        var copy = statusCriteria.copy();

        assertThat(statusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(statusCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var statusCriteria = new StatusCriteria();

        assertThat(statusCriteria).hasToString("StatusCriteria{}");
    }

    private static void setAllFilters(StatusCriteria statusCriteria) {
        statusCriteria.id();
        statusCriteria.name();
        statusCriteria.sequenceNo();
        statusCriteria.description();
        statusCriteria.type();
        statusCriteria.categoryId();
        statusCriteria.distinct();
    }

    private static Condition<StatusCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSequenceNo()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StatusCriteria> copyFiltersAre(StatusCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSequenceNo(), copy.getSequenceNo()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
