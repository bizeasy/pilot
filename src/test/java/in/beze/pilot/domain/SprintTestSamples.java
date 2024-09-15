package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SprintTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Sprint getSprintSample1() {
        return new Sprint().id(1L).name("name1").goal("goal1").totalPoints(1);
    }

    public static Sprint getSprintSample2() {
        return new Sprint().id(2L).name("name2").goal("goal2").totalPoints(2);
    }

    public static Sprint getSprintRandomSampleGenerator() {
        return new Sprint()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .goal(UUID.randomUUID().toString())
            .totalPoints(intCount.incrementAndGet());
    }
}
