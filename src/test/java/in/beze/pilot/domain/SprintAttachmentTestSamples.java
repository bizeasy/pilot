package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SprintAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SprintAttachment getSprintAttachmentSample1() {
        return new SprintAttachment().id(1L);
    }

    public static SprintAttachment getSprintAttachmentSample2() {
        return new SprintAttachment().id(2L);
    }

    public static SprintAttachment getSprintAttachmentRandomSampleGenerator() {
        return new SprintAttachment().id(longCount.incrementAndGet());
    }
}
