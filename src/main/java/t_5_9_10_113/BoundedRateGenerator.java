package t_5_9_10_113;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class BoundedRateGenerator  implements SourceFunction<Tuple2<Integer, Long>>, CheckpointedFunction {

    private static final long serialVersionUID = -2819385275681175792L;

    private static final Random random = new Random();

    private volatile int numRecordsEmitted = 0;
    private volatile boolean canceled = false;

    private ListState<Integer> state = null;

    public BoundedRateGenerator() {

    }

    @Override
    public void run(final SourceContext<Tuple2<Integer, Long>> ctx) throws Exception {
        int numKeys = random.nextInt(10);
            synchronized (ctx.getCheckpointLock()) {
                for (int i = 0; i < numKeys; i++) {
                    ctx.collect(Tuple2.of(i, Long.valueOf("" + numRecordsEmitted)));
                    numRecordsEmitted++;
                }
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
