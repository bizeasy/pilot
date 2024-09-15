package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PartyTypeCriteriaTest {

    @Test
    void newPartyTypeCriteriaHasAllFiltersNullTest() {
        var partyTypeCriteria = new PartyTypeCriteria();
        assertThat(partyTypeCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void partyTypeCriteriaFluentMethodsCreatesFiltersTest() {
        var partyTypeCriteria = new PartyTypeCriteria();

        setAllFilters(partyTypeCriteria);

        assertThat(partyTypeCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void partyTypeCriteriaCopyCreatesNullFilterTest() {
        var partyTypeCriteria = new PartyTypeCriteria();
        var copy = partyTypeCriteria.copy();

        assertThat(partyTypeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(partyTypeCriteria)
        );
    }

    @Test
    void partyTypeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var partyTypeCriteria = new PartyTypeCriteria();
        setAllFilters(partyTypeCriteria);

        var copy = partyTypeCriteria.copy();

        assertThat(partyTypeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(partyTypeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var partyTypeCriteria = new PartyTypeCriteria();

        assertThat(partyTypeCriteria).hasToString("PartyTypeCriteria{}");
    }

    private static void setAllFilters(PartyTypeCriteria partyTypeCriteria) {
        partyTypeCriteria.id();
        partyTypeCriteria.name();
        partyTypeCriteria.description();
        partyTypeCriteria.distinct();
    }

    private static Condition<PartyTypeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PartyTypeCriteria> copyFiltersAre(PartyTypeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
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
