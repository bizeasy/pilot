package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SprintTaskHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SprintTaskHistory getSprintTaskHistorySample1() {
        return new SprintTaskHistory().id(1L).comments("comments1");
    }

    public static SprintTaskHistory getSprintTaskHistorySample2() {
        return new SprintTaskHistory().id(2L).comments("comments2");
    }

    public static SprintTaskHistory getSprintTaskHistoryRandomSampleGenerator() {
        return new SprintTaskHistory().id(longCount.incrementAndGet()).comments(UUID.randomUUID().toString());
    }
}
