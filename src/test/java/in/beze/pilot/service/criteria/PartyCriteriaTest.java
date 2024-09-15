package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PartyCriteriaTest {

    @Test
    void newPartyCriteriaHasAllFiltersNullTest() {
        var partyCriteria = new PartyCriteria();
        assertThat(partyCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void partyCriteriaFluentMethodsCreatesFiltersTest() {
        var partyCriteria = new PartyCriteria();

        setAllFilters(partyCriteria);

        assertThat(partyCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void partyCriteriaCopyCreatesNullFilterTest() {
        var partyCriteria = new PartyCriteria();
        var copy = partyCriteria.copy();

        assertThat(partyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(partyCriteria)
        );
    }

    @Test
    void partyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var partyCriteria = new PartyCriteria();
        setAllFilters(partyCriteria);

        var copy = partyCriteria.copy();

        assertThat(partyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(partyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var partyCriteria = new PartyCriteria();

        assertThat(partyCriteria).hasToString("PartyCriteria{}");
    }

    private static void setAllFilters(PartyCriteria partyCriteria) {
        partyCriteria.id();
        partyCriteria.firstName();
        partyCriteria.lastName();
        partyCriteria.displayName();
        partyCriteria.email();
        partyCriteria.dob();
        partyCriteria.notes();
        partyCriteria.mobileNumber();
        partyCriteria.employeeId();
        partyCriteria.login();
        partyCriteria.userId();
        partyCriteria.statusId();
        partyCriteria.partyTypeId();
        partyCriteria.distinct();
    }

    private static Condition<PartyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getDisplayName()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getDob()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getMobileNumber()) &&
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getLogin()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getPartyTypeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PartyCriteria> copyFiltersAre(PartyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getDisplayName(), copy.getDisplayName()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getDob(), copy.getDob()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getMobileNumber(), copy.getMobileNumber()) &&
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getLogin(), copy.getLogin()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getPartyTypeId(), copy.getPartyTypeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
