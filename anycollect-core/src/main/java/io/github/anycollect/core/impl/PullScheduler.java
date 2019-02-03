package io.github.anycollect.core.impl;

import io.github.anycollect.core.api.internal.State;
import io.github.anycollect.core.api.query.Query;
import io.github.anycollect.core.api.target.Target;

public interface PullScheduler {
    <T extends Target<Q>, Q extends Query> void udpate(Context context, State<T, Q> state);
}
