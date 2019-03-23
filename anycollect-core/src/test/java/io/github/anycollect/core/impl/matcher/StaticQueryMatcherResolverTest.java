package io.github.anycollect.core.impl.matcher;

import io.github.anycollect.core.api.internal.QueryMatcher;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;
import io.github.anycollect.extensions.AnnotationDefinitionLoader;
import io.github.anycollect.extensions.DefinitionLoader;
import io.github.anycollect.extensions.InstanceLoader;
import io.github.anycollect.extensions.definitions.ContextImpl;
import io.github.anycollect.extensions.definitions.Definition;
import io.github.anycollect.extensions.definitions.Instance;
import io.github.anycollect.extensions.snakeyaml.YamlInstanceLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticQueryMatcherResolverTest {
    private StaticQueryMatcherResolver resolver;

    @BeforeEach
    void createPullManager() throws Exception {
        DefinitionLoader definitionLoader = new AnnotationDefinitionLoader(Collections.singletonList(StaticQueryMatcherResolver.class));
        Collection<Definition> definitions = definitionLoader.load();
        File config = FileUtils.getFile("src", "test", "resources", "static-query-matcher-resolver.yaml");
        InstanceLoader instanceLoader = new YamlInstanceLoader(new FileReader(config));
        ContextImpl context = new ContextImpl(definitions);
        instanceLoader.load(context);
        List<Instance> instances = context.getInstances();
        resolver = (StaticQueryMatcherResolver) instances.get(0).resolve();
    }

    @Test
    @DisplayName("is successfully instantiated by extension system")
    void isInstantiatedBySystem() {
        assertThat(resolver).isNotNull();
    }

    @Test
    void mustBeImmutableAndConsistentlyReturnTheSameValue() {
        assertThat(resolver.current()).isSameAs(resolver.current());
    }

    @Test
    void name() {
        QueryMatcher matcher = resolver.current();
        Target users1 = newTarget("users-1");
        Query jvmMemory = newQuery("jvm.memory");
        Query usersLogins = newQuery("users.logins");
        assertThat(matcher.getPeriodInSeconds(users1, jvmMemory, -1)).isEqualTo(30);
        assertThat(matcher.getPeriodInSeconds(users1, usersLogins, -1)).isEqualTo(60);
    }

    private static Target newTarget(String id) {
        Target target = mock(Target.class);
        when(target.getId()).thenReturn(id);
        return target;
    }

    private static Query newQuery(String id) {
        Query query = mock(Query.class);
        when(query.getId()).thenReturn(id);
        return query;
    }
}