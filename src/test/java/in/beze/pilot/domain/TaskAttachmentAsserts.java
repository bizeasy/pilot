package in.beze.pilot.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskAttachmentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAttachmentAllPropertiesEquals(TaskAttachment expected, TaskAttachment actual) {
        assertTaskAttachmentAutoGeneratedPropertiesEquals(expected, actual);
        assertTaskAttachmentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAttachmentAllUpdatablePropertiesEquals(TaskAttachment expected, TaskAttachment actual) {
        assertTaskAttachmentUpdatableFieldsEquals(expected, actual);
        assertTaskAttachmentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAttachmentAutoGeneratedPropertiesEquals(TaskAttachment expected, TaskAttachment actual) {
        assertThat(expected)
            .as("Verify TaskAttachment auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAttachmentUpdatableFieldsEquals(TaskAttachment expected, TaskAttachment actual) {
        // empty method

    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAttachmentUpdatableRelationshipsEquals(TaskAttachment expected, TaskAttachment actual) {
        assertThat(expected)
            .as("Verify TaskAttachment relationships")
            .satisfies(e -> assertThat(e.getTask()).as("check task").isEqualTo(actual.getTask()))
            .satisfies(e -> assertThat(e.getAttachment()).as("check attachment").isEqualTo(actual.getAttachment()));
    }
}
