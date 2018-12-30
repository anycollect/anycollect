package io.github.anycollect.readers.jmx.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialsTest {
    @Test
    void propertiesTest() {
        Credentials credentials = new Credentials("user", "pass");
        assertThat(credentials.getUsername()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("pass");
    }
}