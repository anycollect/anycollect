package io.github.anycollect.core.api.internal;

import io.github.anycollect.core.api.Serializer;
import io.github.anycollect.core.exceptions.SerialisationException;
import io.github.anycollect.metric.Metric;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdaptiveSerializerImplTest {
    @Test
    void mustIncreaseBufferSizeWhenOverflow() throws SerialisationException {
        Serializer under = mock(Serializer.class);
        AdaptiveSerializerImpl serializer = new AdaptiveSerializerImpl(under, 1, 100);
        Metric metric = mock(Metric.class);
        when(under.serialize(eq(metric), any(ByteBuffer.class)))
                .thenAnswer((Answer<CoderResult>) invocation -> {
                    ByteBuffer buffer = invocation.getArgument(1);
                    if (buffer.capacity() < 10) {
                        return CoderResult.OVERFLOW;
                    } else {
                        return CoderResult.UNDERFLOW;
                    }
                });
        ByteBuffer buffer = serializer.serialize(metric);
        assertThat(buffer.capacity()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void mustThrowExceptionWhenSerializationFail() throws SerialisationException {
        Serializer under = mock(Serializer.class);
        AdaptiveSerializerImpl serializer = new AdaptiveSerializerImpl(under, 1, 100);
        Metric metric = mock(Metric.class);
        when(under.serialize(any(), any())).thenReturn(CoderResult.malformedForLength(1));
        assertThatThrownBy(() -> serializer.serialize(metric)).isInstanceOf(SerialisationException.class);
    }

    @Test
    void mustThrowExceptionIsBufferHasNotBeenReleased() throws SerialisationException {
        Serializer under = mock(Serializer.class);
        when(under.serialize(any(), any())).thenReturn(CoderResult.UNDERFLOW);
        AdaptiveSerializerImpl serializer = new AdaptiveSerializerImpl(under, 1, 100);
        Metric metric = mock(Metric.class);
        serializer.serialize(metric);
        assertThatThrownBy(() -> serializer.serialize(metric)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void canRepeatSerializationIfBufferHasBeenReleased() throws SerialisationException {
        Serializer under = mock(Serializer.class);
        when(under.serialize(any(), any())).thenReturn(CoderResult.UNDERFLOW);
        AdaptiveSerializerImpl serializer = new AdaptiveSerializerImpl(under, 1, 100);
        Metric metric = mock(Metric.class);
        ByteBuffer buffer = serializer.serialize(metric);
        serializer.release(buffer);
        assertThatCode(() -> serializer.serialize(metric)).doesNotThrowAnyException();
        serializer.release(buffer);
    }

    @Test
    void mustThrowExceptionIfMaxSizeOfBufferIsNotEnough() throws SerialisationException {
        Serializer under = mock(Serializer.class);
        when(under.serialize(any(), any())).thenReturn(CoderResult.OVERFLOW);
        AdaptiveSerializerImpl serializer = new AdaptiveSerializerImpl(under, 1, 100);
        Metric metric = mock(Metric.class);
        assertThatThrownBy(() -> serializer.serialize(metric)).isInstanceOf(SerialisationException.class);
    }
}