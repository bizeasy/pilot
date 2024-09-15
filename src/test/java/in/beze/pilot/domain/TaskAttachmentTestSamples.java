package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TaskAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskAttachment getTaskAttachmentSample1() {
        return new TaskAttachment().id(1L);
    }

    public static TaskAttachment getTaskAttachmentSample2() {
        return new TaskAttachment().id(2L);
    }

    public static TaskAttachment getTaskAttachmentRandomSampleGenerator() {
        return new TaskAttachment().id(longCount.incrementAndGet());
    }
}
