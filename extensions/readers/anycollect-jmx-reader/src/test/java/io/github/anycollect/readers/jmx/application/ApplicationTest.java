package io.github.anycollect.readers.jmx.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {
    @Test
    void propertiesTest() {
        Application application = new Application(
                "name",
                new SimpleQueryMatcher("group", "label"),
                null,
                null,
                false);
        assertThat(application.getName()).isEqualTo("name");
        assertThat(application.getQueryMatcher()).isEqualTo(new SimpleQueryMatcher("group", "label"));
        assertThat(application.getConcurrencyLevel()).isEmpty();
        assertThat(application.getCredentials()).isEmpty();
        assertThat(application.isSslEnabled()).isFalse();
    }
}