package t_5_9_10_113;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.parquet.avro.ParquetAvroWriters;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

import java.time.Duration;

public class ParquetReflectApp {

    public static void main(String[] args) throws Exception {
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String outputDirectory = parameter.get("outputDirectory");
        StateBackend stateBackend = new FsStateBackend("file:////Users/rajkumar.natarajan/Documents/Coding/misc/streaming-file-sink-demo/5_9_10_113");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment().enableCheckpointing(Duration.ofSeconds(60).toMillis())
                .setStateBackend(stateBackend);

        DataStream<Rate> input = env.addSource(new BoundedRateGenerator()).map(ParquetReflectApp::getRate).keyBy(x -> (x.id % 10));

        FileSink<Rate> sink = FileSink
                .<Rate>forBulkFormat(new Path(outputDirectory), ParquetAvroWriters.forReflectRecord(Rate.class))
                .withBucketAssigner(new KeyBucketAssigner())
                .build();

        input.sinkTo(sink);

        env.execute();
    }

    private static Rate getRate(Tuple2<Integer, Long> x) {
        Rate r =  new Rate(x.f0, x.f1);
        System.out.println("rate is "+ r);
        return r;
    }
}
