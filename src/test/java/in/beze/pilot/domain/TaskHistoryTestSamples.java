package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskHistory getTaskHistorySample1() {
        return new TaskHistory().id(1L).type("type1");
    }

    public static TaskHistory getTaskHistorySample2() {
        return new TaskHistory().id(2L).type("type2");
    }

    public static TaskHistory getTaskHistoryRandomSampleGenerator() {
        return new TaskHistory().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
