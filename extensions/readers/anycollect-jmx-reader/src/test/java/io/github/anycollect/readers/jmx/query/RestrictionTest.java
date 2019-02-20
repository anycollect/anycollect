package io.github.anycollect.readers.jmx.query;

import org.junit.jupiter.api.Test;

import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;

class RestrictionTest {
    @Test
    void alwaysTrue() throws Exception {
        assertThat(Restriction.all().allows(new ObjectName("test:name=test"))).isTrue();
    }
}