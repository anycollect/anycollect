package io.github.anycollect.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;

public final class JavaApplication {
    private static final Logger LOG = LoggerFactory.getLogger(JavaApplication.class);
    private final Class<?> mainClass;
    private final Map<String, String> binaryVmOptions;
    private final List<String> unaryVmOptions;
    private final int jmxPort;
    private volatile Process process = null;

    public static Builder builder() {
        return new Builder();
    }

    private JavaApplication(final Builder builder) {
        this.mainClass = builder.mainClass;
        this.binaryVmOptions = new HashMap<>(builder.binaryVmOptions);
        this.unaryVmOptions = new ArrayList<>(builder.unaryVmOptions);
        this.jmxPort = builder.jmxPort;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void awaitJmx() {
        awaitJmx(5);
    }

    public void awaitJmx(final int seconds) {
        await()
                .atMost(seconds, TimeUnit.SECONDS)
                .until(this::readyToAcceptJmxConnections);
    }

    public boolean readyToAcceptJmxConnections() {
        try {
            JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"
                    + jmxPort
                    + "/jmxrmi"
            );
            JMXConnector connect = JMXConnectorFactory.connect(serviceURL);
            connect.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void start() throws IOException {
        if (process != null) {
            throw new IllegalStateException("process is already running");
        }
        List<String> command = new ArrayList<>(Arrays.asList("java", "-cp", getCurrentClasspath()));
        for (Map.Entry<String, String> property : binaryVmOptions.entrySet()) {
            command.add(property.getKey() + "=" + property.getValue());
        }
        command.addAll(unaryVmOptions);
        command.add(mainClass.getCanonicalName());
        LOG.info("Staring " + command);
        ProcessBuilder processBuilder = new ProcessBuilder().command(command)
                .inheritIO();
        process = processBuilder.start();
    }

    public boolean isRunning() {
        return process != null;
    }

    public boolean isAlive() {
        return isRunning() && process.isAlive();
    }

    public void stop() {
        if (process == null) {
            throw new IllegalStateException("process is not running");
        }
        process.destroy();
        this.process = null;
    }

    private static String getCurrentClasspath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        return Stream.of(urls).map(new Function<URL, String>() {
            @Nullable
            @Override
            public String apply(final URL input) {
                try {
                    return new File(input.toURI()).getPath();
                } catch (URISyntaxException e) {
                    return input.getPath();
                }
            }
        }).collect(Collectors.joining(File.pathSeparator));
    }

    public static void main(final String... args) throws Exception {
        while (true) {
            Thread.sleep(1000);
        }
    }

    public boolean awaitShutdown() {
        return awaitShutdown(5);
    }

    public boolean awaitShutdown(final int seconds) {
        await()
                .atMost(seconds, TimeUnit.SECONDS)
                .until(() -> !this.isAlive());
        return !isAlive();
    }

    public static final class Builder {
        private Class<?> mainClass = JavaApplication.class;
        private final Map<String, String> binaryVmOptions = new HashMap<>();
        private final List<String> unaryVmOptions = new ArrayList<>();
        private int jmxPort = -1;

        public Builder main(final Class<?> mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        public Builder enableJmx(final int jmxPort) {
            this.jmxPort = jmxPort;
            binaryVmOptions.put("-Dcom.sun.management.jmxremote.authenticate", "false");
            binaryVmOptions.put("-Dcom.sun.management.jmxremote.ssl", "false");
            binaryVmOptions.put("-Dcom.sun.management.jmxremote.port", Integer.toString(jmxPort));
            binaryVmOptions.put("-Dcom.sun.management.jmxremote.rmi.port", Integer.toString(jmxPort));
            binaryVmOptions.put("-Djava.rmi.server.hostname", "localhost");
            return this;
        }

        public Builder xms(final int megabytes) {
            unaryVmOptions.add("-Xms" + megabytes + "m");
            return this;
        }

        public Builder xmx(final int megabytes) {
            unaryVmOptions.add("-Xmx" + megabytes + "m");
            return this;
        }

        public JavaApplication build() {
            return new JavaApplication(this);
        }
    }
}
