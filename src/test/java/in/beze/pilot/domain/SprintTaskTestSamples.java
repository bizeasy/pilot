package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SprintTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SprintTask getSprintTaskSample1() {
        return new SprintTask().id(1L).sequenceNo(1).storyPoints(1);
    }

    public static SprintTask getSprintTaskSample2() {
        return new SprintTask().id(2L).sequenceNo(2).storyPoints(2);
    }

    public static SprintTask getSprintTaskRandomSampleGenerator() {
        return new SprintTask()
            .id(longCount.incrementAndGet())
            .sequenceNo(intCount.incrementAndGet())
            .storyPoints(intCount.incrementAndGet());
    }
}
