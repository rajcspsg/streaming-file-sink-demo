package t91;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * Test program for the {@link StreamingFileSink} and {@link FileSink}.
 *
 * <p>Uses a source that steadily emits a deterministic set of records over 60 seconds, after which
 * it idles and waits for job cancellation. Every record has a unique index that is written to the
 * file.
 *
 * <p>The sink rolls on each checkpoint, with each part file containing a sequence of integers.
 * Adding all committed part files together, and numerically sorting the contents, should result in
 * a complete sequence from 0 (inclusive) to 60000 (exclusive).
 */
public class FileSinkProgram {

    public static void main(final String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final String outputPath = params.getRequired("outputPath");
        final String sinkToTest = params.getRequired("sinkToTest");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(4);
        env.enableCheckpointing(5000L);
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(
                        Integer.MAX_VALUE, Time.of(10L, TimeUnit.SECONDS)));

        // generate data, shuffle, sink
        DataStream<Tuple2<Integer, Integer>> source = env.addSource(new Generator(10, 10, 60));

        if (sinkToTest.equalsIgnoreCase("StreamingFileSink")) {
            final StreamingFileSink<Tuple2<Integer, Integer>> sink =
                    StreamingFileSink.forRowFormat(
                                    new Path(outputPath),
                                    (Encoder<Tuple2<Integer, Integer>>)
                                            (element, stream) -> {
                                                PrintStream out = new PrintStream(stream);
                                                out.println(element.f1);
                                            })
                            .withBucketAssigner(new KeyBucketAssigner())
                            .withRollingPolicy(OnCheckpointRollingPolicy.build())
                            .build();

            source.keyBy(0).addSink(sink);
        } else if (sinkToTest.equalsIgnoreCase("FileSink")) {
            FileSink<Tuple2<Integer, Integer>> sink =
                    FileSink.forRowFormat(
                                    new Path(outputPath),
                                    (Encoder<Tuple2<Integer, Integer>>)
                                            (element, stream) -> {
                                                PrintStream out = new PrintStream(stream);
                                                out.println(element.f1);
                                            })
                            .withBucketAssigner(new KeyBucketAssigner())
                            .withRollingPolicy(OnCheckpointRollingPolicy.build())
                            .build();
            source.sinkTo(sink);
        } else {
            throw new UnsupportedOperationException("Unsupported sink type: " + sinkToTest);
        }

        env.execute("StreamingFileSinkProgram");
    }




}