package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IndividualPerformanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static IndividualPerformance getIndividualPerformanceSample1() {
        return new IndividualPerformance().id(1L).completedTasks(1).velocity(1).storyPointsCompleted(1);
    }

    public static IndividualPerformance getIndividualPerformanceSample2() {
        return new IndividualPerformance().id(2L).completedTasks(2).velocity(2).storyPointsCompleted(2);
    }

    public static IndividualPerformance getIndividualPerformanceRandomSampleGenerator() {
        return new IndividualPerformance()
            .id(longCount.incrementAndGet())
            .completedTasks(intCount.incrementAndGet())
            .velocity(intCount.incrementAndGet())
            .storyPointsCompleted(intCount.incrementAndGet());
    }
}
