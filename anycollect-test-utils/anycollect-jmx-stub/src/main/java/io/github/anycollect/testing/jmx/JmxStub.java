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
import oshi.SystemInfo;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public final class JmxStub {
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
    private volatile JmxConfig appliedConfig;
    private volatile long delay = 0;

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
        String node = args[0];
        String serviceId = args[1];
        String consulHost = args[2];
        int consulPort = Integer.parseInt(args[3]);
        Consul client = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts(consulHost, consulPort))
                .build();
        String pidFile = args[4];
        System.out.println("pid file: \"" + pidFile + "\"");
        int pid = new SystemInfo().getOperatingSystem().getProcessId();
        System.out.println("pid: " + pid);
        Files.write(Paths.get(pidFile),
                Collections.singletonList(Integer.toString(pid)),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name("stub")
                .build();
        AgentClient agentClient = client.agentClient();
        agentClient.register(service);

        KeyValueClient kvs = client.keyValueClient();
        KVCache kvCache = KVCache.newCache(kvs, "jmx-stub/");
        JmxStub jmxStub = new JmxStub();
        kvCache.addListener(map -> {
            Value confValue = map.get("conf");
            JmxConfig conf = JmxConfig.empty();
            if (confValue != null) {
                conf = confValue.getValueAsString().map(str -> {
                    try {
                        return YAML_OBJECT_MAPPER.readValue(str, JmxConfig.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).orElse(JmxConfig.empty());
            }
            Value delayValue = map.get(serviceId + "/delay");
            long delay = 0L;
            if (delayValue != null) {
                delay = delayValue.getValueAsString().map(Long::parseLong).orElse(0L);
            }
            jmxStub.configure(conf, delay);
        });
        kvCache.start();
        ImmutableJmxRegistration jmxRegistration = JmxRegistration.builder()
                .id(serviceId)
                .host(host)
                .port(port)
                .build();

        kvs.putValue(
                "/anycollect/jmx/" + node + "/" + serviceId,
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
            try {
                Files.delete(Paths.get(pidFile));
            } catch (IOException e) {
                System.out.println("fail to delete pid file " + pidFile);
            }
            System.out.println("pid file has been successfully deleted");
        }));
    }

    private void configure(final JmxConfig config, final long delay) {
        System.out.println("configure: " + config + " " + delay);
        if (config.equals(appliedConfig) && this.delay == delay) {
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
                        server.registerMBean(new Histogram(delay), objectName);
                    }
                    if (mBeanDefinition.type() == MBeanType.COUNTER) {
                        server.registerMBean(new Counter(delay), objectName);
                    }
                    if (mBeanDefinition.type() == MBeanType.GAUGE) {
                        server.registerMBean(new Gauge(delay), objectName);
                    }
                }
            }
            this.appliedConfig = config;
            this.delay = delay;
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
