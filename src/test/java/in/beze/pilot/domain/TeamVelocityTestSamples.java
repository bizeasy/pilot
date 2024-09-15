package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TeamVelocityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TeamVelocity getTeamVelocitySample1() {
        return new TeamVelocity().id(1L).sprintVelocity(1).averageVelocity(1);
    }

    public static TeamVelocity getTeamVelocitySample2() {
        return new TeamVelocity().id(2L).sprintVelocity(2).averageVelocity(2);
    }

    public static TeamVelocity getTeamVelocityRandomSampleGenerator() {
        return new TeamVelocity()
            .id(longCount.incrementAndGet())
            .sprintVelocity(intCount.incrementAndGet())
            .averageVelocity(intCount.incrementAndGet());
    }
}
