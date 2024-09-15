package in.beze.pilot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PartyAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PartyAttachment getPartyAttachmentSample1() {
        return new PartyAttachment().id(1L);
    }

    public static PartyAttachment getPartyAttachmentSample2() {
        return new PartyAttachment().id(2L);
    }

    public static PartyAttachment getPartyAttachmentRandomSampleGenerator() {
        return new PartyAttachment().id(longCount.incrementAndGet());
    }
}
