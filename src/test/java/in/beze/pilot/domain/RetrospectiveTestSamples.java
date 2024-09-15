package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RetrospectiveTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Retrospective getRetrospectiveSample1() {
        return new Retrospective().id(1L).summary("summary1").actionItems("actionItems1");
    }

    public static Retrospective getRetrospectiveSample2() {
        return new Retrospective().id(2L).summary("summary2").actionItems("actionItems2");
    }

    public static Retrospective getRetrospectiveRandomSampleGenerator() {
        return new Retrospective()
            .id(longCount.incrementAndGet())
            .summary(UUID.randomUUID().toString())
            .actionItems(UUID.randomUUID().toString());
    }
}
