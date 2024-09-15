package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StatusCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StatusCategory getStatusCategorySample1() {
        return new StatusCategory().id(1L).name("name1").description("description1");
    }

    public static StatusCategory getStatusCategorySample2() {
        return new StatusCategory().id(2L).name("name2").description("description2");
    }

    public static StatusCategory getStatusCategoryRandomSampleGenerator() {
        return new StatusCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
