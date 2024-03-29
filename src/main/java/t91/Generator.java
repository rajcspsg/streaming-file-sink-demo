package t91;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/** Data-generating source function. */
public final class Generator implements SourceFunction<Tuple2<Integer, Integer>>, CheckpointedFunction {

    private static final long serialVersionUID = -2819385275681175792L;

    private final int numKeys;
    private final int idlenessMs;
    private final int recordsToEmit;

    private volatile int numRecordsEmitted = 0;
    private volatile boolean canceled = false;

    private ListState<Integer> state = null;

    Generator(final int numKeys, final int idlenessMs, final int durationSeconds) {
        this.numKeys = numKeys;
        this.idlenessMs = idlenessMs;

        this.recordsToEmit = ((durationSeconds * 1000) / idlenessMs) * numKeys;
    }

    @Override
    public void run(final SourceContext<Tuple2<Integer, Integer>> ctx) throws Exception {
        while (numRecordsEmitted < recordsToEmit) {
            synchronized (ctx.getCheckpointLock()) {
                for (int i = 0; i < numKeys; i++) {
                    ctx.collect(Tuple2.of(i, numRecordsEmitted));
                    numRecordsEmitted++;
                }
            }
            Thread.sleep(idlenessMs);
        }

        while (!canceled) {
            Thread.sleep(50);
        }
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        state =
                context.getOperatorStateStore()
                        .getListState(
                                new ListStateDescriptor<Integer>(
                                        "state", IntSerializer.INSTANCE));

        for (Integer i : state.get()) {
            numRecordsEmitted += i;
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        state.clear();
        state.add(numRecordsEmitted);
    }
}
