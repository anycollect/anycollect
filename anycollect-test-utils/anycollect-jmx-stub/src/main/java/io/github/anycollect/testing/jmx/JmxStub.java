package io.github.anycollect.testing.jmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.kv.Value;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

public final class JmxStub {
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
    private volatile JmxConfig appliedConfig;

    static {
        JSON_OBJECT_MAPPER.registerModule(new GuavaModule());
        YAML_OBJECT_MAPPER.registerModule(new GuavaModule());
    }

    private JmxStub() {
        this.appliedConfig = JmxConfig.empty();
    }

    public static void main(final String... args) throws Exception {
        String host = System.getProperty("java.rmi.server.hostname");
        String port = System.getProperty("com.sun.management.jmxremote.rmi.port");
        System.out.println("jmx host: " + host + ", port: " + port);
        String serviceId = args[0];
        String consulHost = args[1];
        int consulPort = Integer.parseInt(args[2]);
        Consul client = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts(consulHost, consulPort))
                .build();
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name("stub")
                .build();
        AgentClient agentClient = client.agentClient();
        agentClient.register(service);

        KeyValueClient kvs = client.keyValueClient();
        KVCache kvCache = KVCache.newCache(kvs, "jmx-stub/conf");
        JmxStub jmxStub = new JmxStub();
        kvCache.addListener(map -> {
            Value value = map.get("");
            JmxConfig conf = value.getValueAsString().map(str -> {
                try {
                    return YAML_OBJECT_MAPPER.readValue(str, JmxConfig.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).orElse(JmxConfig.empty());
            jmxStub.configure(conf);
        });
        kvCache.start();
        ImmutableJmxRegistration jmxRegistration = JmxRegistration.builder()
                .id(serviceId)
                .host(host)
                .port(port)
                .build();

        kvs.putValue(
                "/anycollect/jmx/" + serviceId,
                JSON_OBJECT_MAPPER.writeValueAsString(jmxRegistration)
        );

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
            kvCache.stop();
            kvs.deleteKey("/anycollect/jmx/" + serviceId);
            thread.interrupt();
            System.out.println("service has been successfully deregistered from consul");
        }));
    }

    private void configure(final JmxConfig config) {
        System.out.println("configure: " + config);
        if (config.equals(appliedConfig)) {
            System.out.println("no changes");
            return;
        }
        String domain = config.domain();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            for (MBeanDefinition mBeanDefinition : appliedConfig.mbeans()) {
                List<ObjectName> objectNames = resolve(domain, mBeanDefinition);
                for (ObjectName objectName : objectNames) {
                    System.out.println("unregister " + objectName);
                    server.unregisterMBean(objectName);
                }
            }
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
            appliedConfig = config;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
