package in.beze.pilot.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyAttachmentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyAttachmentAllPropertiesEquals(PartyAttachment expected, PartyAttachment actual) {
        assertPartyAttachmentAutoGeneratedPropertiesEquals(expected, actual);
        assertPartyAttachmentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyAttachmentAllUpdatablePropertiesEquals(PartyAttachment expected, PartyAttachment actual) {
        assertPartyAttachmentUpdatableFieldsEquals(expected, actual);
        assertPartyAttachmentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyAttachmentAutoGeneratedPropertiesEquals(PartyAttachment expected, PartyAttachment actual) {
        assertThat(expected)
            .as("Verify PartyAttachment auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyAttachmentUpdatableFieldsEquals(PartyAttachment expected, PartyAttachment actual) {
        // empty method

    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPartyAttachmentUpdatableRelationshipsEquals(PartyAttachment expected, PartyAttachment actual) {
        assertThat(expected)
            .as("Verify PartyAttachment relationships")
            .satisfies(e -> assertThat(e.getParty()).as("check party").isEqualTo(actual.getParty()))
            .satisfies(e -> assertThat(e.getAttachment()).as("check attachment").isEqualTo(actual.getAttachment()));
    }
}
