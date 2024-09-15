package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PartyAttachmentCriteriaTest {

    @Test
    void newPartyAttachmentCriteriaHasAllFiltersNullTest() {
        var partyAttachmentCriteria = new PartyAttachmentCriteria();
        assertThat(partyAttachmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void partyAttachmentCriteriaFluentMethodsCreatesFiltersTest() {
        var partyAttachmentCriteria = new PartyAttachmentCriteria();

        setAllFilters(partyAttachmentCriteria);

        assertThat(partyAttachmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void partyAttachmentCriteriaCopyCreatesNullFilterTest() {
        var partyAttachmentCriteria = new PartyAttachmentCriteria();
        var copy = partyAttachmentCriteria.copy();

        assertThat(partyAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(partyAttachmentCriteria)
        );
    }

    @Test
    void partyAttachmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var partyAttachmentCriteria = new PartyAttachmentCriteria();
        setAllFilters(partyAttachmentCriteria);

        var copy = partyAttachmentCriteria.copy();

        assertThat(partyAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(partyAttachmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var partyAttachmentCriteria = new PartyAttachmentCriteria();

        assertThat(partyAttachmentCriteria).hasToString("PartyAttachmentCriteria{}");
    }

    private static void setAllFilters(PartyAttachmentCriteria partyAttachmentCriteria) {
        partyAttachmentCriteria.id();
        partyAttachmentCriteria.partyId();
        partyAttachmentCriteria.attachmentId();
        partyAttachmentCriteria.distinct();
    }

    private static Condition<PartyAttachmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPartyId()) &&
                condition.apply(criteria.getAttachmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PartyAttachmentCriteria> copyFiltersAre(
        PartyAttachmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPartyId(), copy.getPartyId()) &&
                condition.apply(criteria.getAttachmentId(), copy.getAttachmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
