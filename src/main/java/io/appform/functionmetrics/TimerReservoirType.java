package io.appform.functionmetrics;

/**
 * A {@link com.codahale.metrics.Timer} uses a {@link com.codahale.metrics.Reservoir} to store its state.
 * Reservoirs can be of two types - decaying and sliding.
 * Sliding window reservoirs provide accurate statistics and work very well in practice.
 * Decaying reservoirs use an exponential function to assign more weight to recent samples.
 * In some performance sensitive workloads that involve highly concurrent updates to the timer,
 * it is preferable to use a decaying reservoir to avoid the extra synchronization and memory overheads
 * that a sliding window reservoir incurs.
  */
public enum TimerReservoirType {
    DECAYING,
    SLIDING
}
