package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StatusCategoryCriteriaTest {

    @Test
    void newStatusCategoryCriteriaHasAllFiltersNullTest() {
        var statusCategoryCriteria = new StatusCategoryCriteria();
        assertThat(statusCategoryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void statusCategoryCriteriaFluentMethodsCreatesFiltersTest() {
        var statusCategoryCriteria = new StatusCategoryCriteria();

        setAllFilters(statusCategoryCriteria);

        assertThat(statusCategoryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void statusCategoryCriteriaCopyCreatesNullFilterTest() {
        var statusCategoryCriteria = new StatusCategoryCriteria();
        var copy = statusCategoryCriteria.copy();

        assertThat(statusCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(statusCategoryCriteria)
        );
    }

    @Test
    void statusCategoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var statusCategoryCriteria = new StatusCategoryCriteria();
        setAllFilters(statusCategoryCriteria);

        var copy = statusCategoryCriteria.copy();

        assertThat(statusCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(statusCategoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var statusCategoryCriteria = new StatusCategoryCriteria();

        assertThat(statusCategoryCriteria).hasToString("StatusCategoryCriteria{}");
    }

    private static void setAllFilters(StatusCategoryCriteria statusCategoryCriteria) {
        statusCategoryCriteria.id();
        statusCategoryCriteria.name();
        statusCategoryCriteria.description();
        statusCategoryCriteria.distinct();
    }

    private static Condition<StatusCategoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StatusCategoryCriteria> copyFiltersAre(
        StatusCategoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
