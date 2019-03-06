package io.github.anycollect.testing.jmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

public final class JmxStub {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    static {
        OBJECT_MAPPER.registerModule(new GuavaModule());
    }

    private JmxStub() {
    }

    public static void main(final String... args) throws Exception {
        String host = System.getProperty("java.rmi.server.hostname");
        String port = System.getProperty("com.sun.management.jmxremote.rmi.port");
        System.out.println("jmx host: " + host + ", port: " + port);

        String serviceId = args[0];
        Consul client = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts("consul", 8500))
                .build();
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name("stub")
                .build();
        AgentClient agentClient = client.agentClient();
        agentClient.register(service);

        KeyValueClient kvs = client.keyValueClient();
        JmxConfig config = kvs.getValueAsString("jmx-stub/conf")
                .map(str -> {
                    try {
                        return OBJECT_MAPPER.readValue(str, JmxConfig.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .orElse(JmxConfig.empty());
        ImmutableJmxRegistration jmxRegistration = JmxRegistration.builder()
                .id(serviceId)
                .host(host)
                .port(port)
                .build();

        kvs.putValue(
                "/anycollect/jmx/" + serviceId,
                OBJECT_MAPPER.writeValueAsString(jmxRegistration)
        );

        String domain = config.domain();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        for (MBeanDefinition mBeanDefinition : config.mbeans()) {
            List<ObjectName> objectNames = resolve(domain, mBeanDefinition);
            for (ObjectName objectName : objectNames) {
                System.out.println("register " + objectName);
                if (mBeanDefinition.type() == MBeanType.HISTOGRAM) {
                    server.registerMBean(new Histogram(), objectName);
                }
                if (mBeanDefinition.type() == MBeanType.COUNTER) {
                    server.registerMBean(new Counter(), objectName);
                }
                if (mBeanDefinition.type() == MBeanType.GAUGE) {
                    server.registerMBean(new Gauge(), objectName);
                }
            }
        }
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("stopping thread");
                        return;
                    }
                } catch (InterruptedException e) {
                    System.out.println("stopping thread");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        thread.setDaemon(false);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("graceful shutdown");
            agentClient.deregister(serviceId);
            kvs.deleteKey("/anycollect/jmx/" + serviceId);
            thread.interrupt();
            System.out.println("service has been successfully deregistered from consul");
        }));
    }

    private static List<ObjectName> resolve(final String domain, final MBeanDefinition mBeanDefinition)
            throws Exception {
        Set<Map.Entry<String, List<String>>> entries = mBeanDefinition.keys().entrySet();
        List<Map.Entry<String, List<String>>> list = new ArrayList<>(entries);
        ArrayList<ObjectName> accumulator = new ArrayList<>();
        resolve(domain, new HashMap<>(), list, accumulator, 0);
        return accumulator;
    }

    private static void resolve(final String domain,
                                final Map<String, String> properties,
                                final List<Map.Entry<String, List<String>>> entries,
                                final List<ObjectName> accumulator,
                                final int index) throws Exception {
        if (index == entries.size()) {
            ObjectName objectName = new ObjectName(domain, new Hashtable<>(properties));
            accumulator.add(objectName);
            return;
        }
        Map.Entry<String, List<String>> entry = entries.get(index);
        String key = entry.getKey();
        List<String> values = entry.getValue();
        for (String value : values) {
            properties.put(key, value);
            resolve(domain, properties, entries, accumulator, index + 1);
            properties.remove(key, value);
        }
    }
}
