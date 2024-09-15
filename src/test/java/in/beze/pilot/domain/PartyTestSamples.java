package in.beze.pilot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Party getPartySample1() {
        return new Party()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .displayName("displayName1")
            .email("email1")
            .notes("notes1")
            .mobileNumber("mobileNumber1")
            .employeeId("employeeId1")
            .login("login1");
    }

    public static Party getPartySample2() {
        return new Party()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .displayName("displayName2")
            .email("email2")
            .notes("notes2")
            .mobileNumber("mobileNumber2")
            .employeeId("employeeId2")
            .login("login2");
    }

    public static Party getPartyRandomSampleGenerator() {
        return new Party()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .displayName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .mobileNumber(UUID.randomUUID().toString())
            .employeeId(UUID.randomUUID().toString())
            .login(UUID.randomUUID().toString());
    }
}
