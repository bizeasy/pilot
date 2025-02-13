package in.beze.pilot.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamVelocityAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamVelocityAllPropertiesEquals(TeamVelocity expected, TeamVelocity actual) {
        assertTeamVelocityAutoGeneratedPropertiesEquals(expected, actual);
        assertTeamVelocityAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamVelocityAllUpdatablePropertiesEquals(TeamVelocity expected, TeamVelocity actual) {
        assertTeamVelocityUpdatableFieldsEquals(expected, actual);
        assertTeamVelocityUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamVelocityAutoGeneratedPropertiesEquals(TeamVelocity expected, TeamVelocity actual) {
        assertThat(expected)
            .as("Verify TeamVelocity auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamVelocityUpdatableFieldsEquals(TeamVelocity expected, TeamVelocity actual) {
        assertThat(expected)
            .as("Verify TeamVelocity relevant properties")
            .satisfies(e -> assertThat(e.getSprintVelocity()).as("check sprintVelocity").isEqualTo(actual.getSprintVelocity()))
            .satisfies(e -> assertThat(e.getAverageVelocity()).as("check averageVelocity").isEqualTo(actual.getAverageVelocity()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamVelocityUpdatableRelationshipsEquals(TeamVelocity expected, TeamVelocity actual) {
        assertThat(expected)
            .as("Verify TeamVelocity relationships")
            .satisfies(e -> assertThat(e.getSprint()).as("check sprint").isEqualTo(actual.getSprint()));
    }
}
