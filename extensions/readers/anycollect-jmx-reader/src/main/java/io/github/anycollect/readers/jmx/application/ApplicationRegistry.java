package io.github.anycollect.readers.jmx.application;

import javax.annotation.Nonnull;
import java.util.*;

public final class ApplicationRegistry {
    private static final ApplicationRegistry EMPTY = new ApplicationRegistry(Collections.emptyMap());
    private final Map<String, Application> applications;

    public ApplicationRegistry(@Nonnull final List<Application> applications) {
        Objects.requireNonNull(applications, "applications must not be null");
        Map<String, Application> tmp = new HashMap<>();
        for (Application application : applications) {
            String appName = application.getName();
            if (tmp.containsKey(appName)) {
                throw new IllegalArgumentException("there are two applications: "
                        + tmp.get(appName) + " and " + application
                        + " with name " + appName);
            }
            tmp.put(application.getName(), application);
        }
        this.applications = tmp;
    }

    private ApplicationRegistry(@Nonnull final Map<String, Application> applications) {
        this.applications = applications;
    }

    @Nonnull
    public static ApplicationRegistry singleton(@Nonnull final Application application) {
        return new ApplicationRegistry(Collections.singletonMap(application.getName(), application));
    }

    @Nonnull
    public static ApplicationRegistry empty() {
        return EMPTY;
    }

    public boolean hasApplication(@Nonnull final String name) {
        return applications.containsKey(name);
    }

    @Nonnull
    public Application getApplication(@Nonnull final String name) {
        if (!hasApplication(name)) {
            throw new IllegalArgumentException("there is no application with name " + name + " in registry");
        }
        return applications.get(name);
    }
}
