package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProjectAttachment getProjectAttachmentSample1() {
        return new ProjectAttachment().id(1L);
    }

    public static ProjectAttachment getProjectAttachmentSample2() {
        return new ProjectAttachment().id(2L);
    }

    public static ProjectAttachment getProjectAttachmentRandomSampleGenerator() {
        return new ProjectAttachment().id(longCount.incrementAndGet());
    }
}
