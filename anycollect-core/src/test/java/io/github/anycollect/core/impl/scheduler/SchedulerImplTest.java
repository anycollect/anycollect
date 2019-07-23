package io.github.anycollect.core.impl.scheduler;

import io.github.anycollect.core.api.internal.Cancellation;
import org.junit.jupiter.api.*;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("scheduler")
class SchedulerImplTest {
    private ScheduledThreadPoolExecutor executorService = mock(ScheduledThreadPoolExecutor.class);
    private SchedulerImpl scheduler;

    @BeforeEach
    void createScheduler() {
        scheduler = new SchedulerImpl(executorService);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @Test
        @DisplayName("is not shutdown")
        void isNotShutdown() {
            assertThat(scheduler.isShutdown()).isFalse();
        }

        @Nested
        @DisplayName("when schedule job")
        class WhenScheduleJob {
            private Cancellation cancellation;
            private Runnable job = () -> {
            };
            private ScheduledFuture future;

            @BeforeEach
            @SuppressWarnings("unchecked")
            void schedule() {
                when(executorService.scheduleAtFixedRate(job, 0L, 10L, TimeUnit.MILLISECONDS)).thenAnswer((Answer<ScheduledFuture<?>>) invocation -> {
                    future = mock(ScheduledFuture.class);
                    return future;
                });
                cancellation = scheduler.scheduleAtFixedRate(job, 0L, 10L, TimeUnit.MILLISECONDS);
            }

            @Test
            @DisplayName("must submit job to executor")
            void mustSubmitJobToExecutor() {
                verify(executorService, times(1)).scheduleAtFixedRate(job, 0, 10, TimeUnit.MILLISECONDS);
            }

            @Nested
            @DisplayName("when cancel")
            class WhenCancel {
                @BeforeEach
                void cancel() {
                    cancellation.cancel();
                }

                @Test
                @DisplayName("must cancel future")
                void mustCancelFuture() {
                    verify(future, times(1)).cancel(true);
                }
            }
        }

        @Nested
        @DisplayName("when shutdown")
        class WhenShutdown {
            @BeforeEach
            void shutdown() {
                when(executorService.isShutdown()).thenReturn(true);
                scheduler.shutdown();
            }

            @Test
            @DisplayName("executor is shutdown")
            void executorIsShutdown() {
                verify(executorService, times(1)).shutdown();
            }

            @Test
            @DisplayName("is shutdown")
            void isShutdown() {
                assertThat(scheduler.isShutdown()).isTrue();
            }

            @Test
            @DisplayName("must not accept new jobs")
            void mustNotAcceptNewJobs() {
                Assertions.assertThrows(IllegalStateException.class, () -> scheduler.scheduleAtFixedRate(() -> {
                }, 10, TimeUnit.SECONDS));
            }
        }
    }
}