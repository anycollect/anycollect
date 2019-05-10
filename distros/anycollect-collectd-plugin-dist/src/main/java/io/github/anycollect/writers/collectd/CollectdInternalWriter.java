package io.github.anycollect.writers.collectd;

import io.github.anycollect.core.api.Writer;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.core.exceptions.ConfigurationException;
import io.github.anycollect.extensions.annotations.ExtConfig;
import io.github.anycollect.extensions.annotations.ExtCreator;
import io.github.anycollect.extensions.annotations.Extension;
import io.github.anycollect.extensions.annotations.InstanceId;
import io.github.anycollect.extensions.common.expression.EvaluationException;
import io.github.anycollect.extensions.common.expression.ExpressionFactory;
import io.github.anycollect.extensions.common.expression.ParseException;
import io.github.anycollect.extensions.common.expression.std.StdExpressionFactory;
import io.github.anycollect.metric.Metric;
import org.collectd.api.Collectd;
import org.collectd.api.ValueList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Extension(name = CollectdInternalWriter.NAME, point = Writer.class)
public class CollectdInternalWriter implements Writer {
    public static final String NAME = "CollectdInternalWriter";
    private final List<ValueListTransformer> transformers;
    private final String id;

    @ExtCreator
    public CollectdInternalWriter(@ExtConfig @Nonnull final CollectdConfig config,
                                  @InstanceId @Nonnull final String instanceId) {
        this.id = instanceId;
        ExpressionFactory factory = new StdExpressionFactory();
        this.transformers = new ArrayList<>();
        for (MappingConfig mapping : config.mappings()) {
            ValueListTransformer transformer;
            try {
                transformer = new ValueListTransformer(factory, mapping);
            } catch (ParseException e) {
                throw new ConfigurationException("could not apply mapping " + mapping, e);
            }
            transformers.add(transformer);
        }
    }

    @Override
    public void write(@Nonnull final List<? extends Metric> metrics) {
        for (Metric metric : metrics) {
            for (ValueListTransformer transformer : transformers) {
                if (transformer.getFilter().accept(metric.getFrame()) == FilterReply.ACCEPT) {
                    try {
                        for (ValueList valueList : transformer.transform(metric)) {
                            Collectd.dispatchValues(valueList);
                        }
                    } catch (EvaluationException e) {
                        // TODO
                        // maybe next transformer can deal with it?
                        continue;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
