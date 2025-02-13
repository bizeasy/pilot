package in.beze.pilot.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyTypeAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyTypeAllPropertiesEquals(PartyType expected, PartyType actual) {
        assertPartyTypeAutoGeneratedPropertiesEquals(expected, actual);
        assertPartyTypeAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyTypeAllUpdatablePropertiesEquals(PartyType expected, PartyType actual) {
        assertPartyTypeUpdatableFieldsEquals(expected, actual);
        assertPartyTypeUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyTypeAutoGeneratedPropertiesEquals(PartyType expected, PartyType actual) {
        assertThat(expected)
            .as("Verify PartyType auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyTypeUpdatableFieldsEquals(PartyType expected, PartyType actual) {
        assertThat(expected)
            .as("Verify PartyType relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyTypeUpdatableRelationshipsEquals(PartyType expected, PartyType actual) {
        // empty method
    }
}
