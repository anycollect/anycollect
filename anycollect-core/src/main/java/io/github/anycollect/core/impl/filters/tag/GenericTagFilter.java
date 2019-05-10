package io.github.anycollect.core.impl.filters.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.anycollect.core.api.filter.Filter;
import io.github.anycollect.core.api.filter.FilterReply;
import io.github.anycollect.metric.frame.MetricFrame;

import javax.annotation.Nonnull;

@JsonTypeName("tag")
@JsonDeserialize(builder = GenericTagFilter.Builder.class)
public final class GenericTagFilter implements Filter {
    private final FilterReply reply;
    private final String key;
    private final TagExistence state;

    private GenericTagFilter(final Builder builder) {
        this.reply = builder.reply;
        this.key = builder.key;
        this.state = builder.state;
    }

    @Override
    public FilterReply accept(@Nonnull final MetricFrame frame) {
        if (state == TagExistence.PRESENT) {
            return frame.getTags().hasTagKey(key) ? reply : FilterReply.NEUTRAL;
        } else {
            return !frame.getTags().hasTagKey(key) ? reply : FilterReply.NEUTRAL;
        }
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private FilterReply reply = FilterReply.ACCEPT;
        private String key;
        private TagExistence state = TagExistence.PRESENT;

        @JsonProperty(value = "key", required = true)
        public Builder key(final String key) {
            this.key = key;
            return this;
        }

        @JsonProperty("reply")
        public Builder reply(final FilterReply reply) {
            this.reply = reply;
            return this;
        }

        @JsonProperty("state")
        public Builder state(final TagExistence state) {
            this.state = state;
            return this;
        }

        public GenericTagFilter build() {
            return new GenericTagFilter(this);
        }
    }
}
