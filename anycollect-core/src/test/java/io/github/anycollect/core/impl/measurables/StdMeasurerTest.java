package io.github.anycollect.core.impl.measurables;

import io.github.anycollect.core.api.measurable.FamilyConfig;
import io.github.anycollect.core.api.measurable.Measurable;
import io.github.anycollect.core.exceptions.QueryException;
import io.github.anycollect.metric.MetricFamily;
import io.github.anycollect.metric.Stat;
import io.github.anycollect.metric.Tags;
import io.github.anycollect.metric.Type;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.anycollect.assertj.AnyCollectAssertions.assertThat;
import static org.mockito.Mockito.*;

class StdMeasurerTest {

    @Test
    @DisplayName("paths must contain unit paths as well as value paths")
    void pathsMustContainUnitPathsAsWellAsValuePaths() {
        FamilyConfig cfg = new FamilyConfig(
                "key",
                null,
                null,
                null,
                null,
                "sample"
        );
        MeasurementDefinition withUnitPath = new MeasurementDefinition(
                "id", "measurementPath", Stat.min(), Type.GAUGE, "unitPath"
        );
        MeasurementDefinition withoutUnitPath = new MeasurementDefinition(
                "id", "measurementPath", Stat.min(), Type.GAUGE, null
        );
        StdMeasurer measurer = new StdMeasurer(cfg, Lists.list(withUnitPath, withoutUnitPath));
        assertThat(measurer.getPaths()).containsExactlyInAnyOrder("unitPath", "measurementPath");
    }

    private final String baseUnit = "ns";
    private FamilyConfig familyConfig;

    @BeforeEach
    void configure() {
        familyConfig = new FamilyConfig(
                "key",
                baseUnit,
                Sets.newLinkedHashSet("tag1", "tag2"),
                Tags.of("tag3", "value3"),
                Tags.of("meta", "data"),
                "sample"
        );
    }

    @Nested
    @DisplayName("when measure")
    class WhenMeasure {
        private StdMeasurer measurer;
        private Measurable measurable;
        private MetricFamily metricFamily;
        private String unitFromPath = "ms";

        @BeforeEach
        void createMeasurer() throws QueryException {
            MeasurementDefinition min = new MeasurementDefinition(
                    "min", "Min", Stat.MIN, Type.GAUGE, null
            );
            MeasurementDefinition max = new MeasurementDefinition(
                    "max", "Max", Stat.MAX, Type.GAUGE, "MaxUnit"
            );
            measurer = new StdMeasurer(familyConfig, Lists.list(min, max));
            measurable = mock(Measurable.class);
            when(measurable.getTag("tag1")).thenReturn("value1");
            when(measurable.getTag("tag2")).thenReturn("value2");
            when(measurable.getValue("Min")).thenReturn(1.0);
            when(measurable.getValue("Max")).thenReturn(3.0);
            when(measurable.getUnit("MaxUnit")).thenReturn(unitFromPath);
            metricFamily = measurer.measure(measurable, 1);
        }

        @Test
        @DisplayName("metric family tag values has been extracted from measurable by given tag keys")
        void metricFamilyTagValuesHasBeenExtractedFromMeasurableByTagKeys() {
            verify(measurable, times(1)).getTag("tag1");
            verify(measurable, times(1)).getTag("tag2");
        }

        @Nested
        @DisplayName("when there is a path to extract unit")
        class WhenThereIsPathForUnit {
            @Test
            @DisplayName("unit has been extracted from measurable by given path")
            void unitHasBeenExtractedFromMeasurable() {
                verify(measurable, times(1)).getUnit("MaxUnit");
            }

            @Test
            @DisplayName("has measurement with correct unit extracted from path")
            void hasMeasurementWithCorrectUnit() {
                assertThat(metricFamily).hasMeasurement(Stat.MAX, Type.GAUGE, unitFromPath, 3.0);
            }
        }

        @Nested
        @DisplayName("when there is no path for unit")
        class WhenThereIsNoPathForUnit {
            @Test
            @DisplayName("use base unit")
            void hasMeasurementWithBaseUnit() {
                assertThat(metricFamily).hasMeasurement(Stat.MIN, Type.GAUGE, baseUnit, 1.0);
            }
        }

        @Nested
        @DisplayName("when measurable has tags for all tag keys")
        class WhenMeasurableHasAllTags {
            @Test
            @DisplayName("metric family has all configured tags with correct values")
            void metricFamilyHasConfiguredTags() {
                assertThat(metricFamily.getTags()).hasTags(
                        "tag1", "value1",
                        "tag2", "value2",
                        "tag3", "value3"
                );
            }
        }
    }
}