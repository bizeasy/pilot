package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartyTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PartyType getPartyTypeSample1() {
        return new PartyType().id(1L).name("name1").description("description1");
    }

    public static PartyType getPartyTypeSample2() {
        return new PartyType().id(2L).name("name2").description("description2");
    }

    public static PartyType getPartyTypeRandomSampleGenerator() {
        return new PartyType().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
