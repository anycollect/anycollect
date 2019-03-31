package io.github.anycollect.core.impl.transform.transformations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.anycollect.core.impl.transform.SourceTagAction;
import io.github.anycollect.metric.Metric;
import io.github.anycollect.metric.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public final class TagTransformation implements Transformation {
    private final SourceTagAction sourceTagAction;
    private final Key key;
    private final List<Value> values;

    @JsonCreator
    public TagTransformation(@JsonProperty("source") @Nonnull final SourceTagAction sourceTagAction,
                             @JsonProperty("key") @Nonnull final Key key,
                             @JsonProperty("values") @Nonnull final List<Value> values) {
        this.sourceTagAction = sourceTagAction;
        this.key = key;
        this.values = values;
    }

    @Nonnull
    @Override
    public Metric transform(@Nonnull final Metric source) {
        if (!source.getTags().hasTagKey(key.getSource())) {
            return source;
        }
        String sourceValue = source.getTags().getTagValue(key.getSource());
        String targetValue = null;
        for (Value value : values) {
            targetValue = value.transform(sourceValue);
            if (targetValue != null) {
                break;
            }
        }
        if (targetValue == null) {
            targetValue = sourceValue;
        }
        String sourceKey = key.getSource();
        String targetKey = key.getTarget();
        Metric target = source;
        if (sourceTagAction == SourceTagAction.META) {
            target = target.backMeta(Tags.of(sourceKey, sourceValue));
        }
        if (sourceTagAction != SourceTagAction.KEEP) {
            target = target.removeTag(sourceKey);
        }
        target = target.backTags(Tags.of(targetKey, targetValue));
        return target;
    }
}
