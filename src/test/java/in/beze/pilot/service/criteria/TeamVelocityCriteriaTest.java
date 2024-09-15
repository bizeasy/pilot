package in.beze.pilot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TeamVelocityCriteriaTest {

    @Test
    void newTeamVelocityCriteriaHasAllFiltersNullTest() {
        var teamVelocityCriteria = new TeamVelocityCriteria();
        assertThat(teamVelocityCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void teamVelocityCriteriaFluentMethodsCreatesFiltersTest() {
        var teamVelocityCriteria = new TeamVelocityCriteria();

        setAllFilters(teamVelocityCriteria);

        assertThat(teamVelocityCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void teamVelocityCriteriaCopyCreatesNullFilterTest() {
        var teamVelocityCriteria = new TeamVelocityCriteria();
        var copy = teamVelocityCriteria.copy();

        assertThat(teamVelocityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(teamVelocityCriteria)
        );
    }

    @Test
    void teamVelocityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var teamVelocityCriteria = new TeamVelocityCriteria();
        setAllFilters(teamVelocityCriteria);

        var copy = teamVelocityCriteria.copy();

        assertThat(teamVelocityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(teamVelocityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var teamVelocityCriteria = new TeamVelocityCriteria();

        assertThat(teamVelocityCriteria).hasToString("TeamVelocityCriteria{}");
    }

    private static void setAllFilters(TeamVelocityCriteria teamVelocityCriteria) {
        teamVelocityCriteria.id();
        teamVelocityCriteria.sprintVelocity();
        teamVelocityCriteria.averageVelocity();
        teamVelocityCriteria.sprintId();
        teamVelocityCriteria.distinct();
    }

    private static Condition<TeamVelocityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSprintVelocity()) &&
                condition.apply(criteria.getAverageVelocity()) &&
                condition.apply(criteria.getSprintId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TeamVelocityCriteria> copyFiltersAre(
        TeamVelocityCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSprintVelocity(), copy.getSprintVelocity()) &&
                condition.apply(criteria.getAverageVelocity(), copy.getAverageVelocity()) &&
                condition.apply(criteria.getSprintId(), copy.getSprintId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
