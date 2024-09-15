package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskLog getTaskLogSample1() {
        return new TaskLog().id(1L).comments("comments1");
    }

    public static TaskLog getTaskLogSample2() {
        return new TaskLog().id(2L).comments("comments2");
    }

    public static TaskLog getTaskLogRandomSampleGenerator() {
        return new TaskLog().id(longCount.incrementAndGet()).comments(UUID.randomUUID().toString());
    }
}
