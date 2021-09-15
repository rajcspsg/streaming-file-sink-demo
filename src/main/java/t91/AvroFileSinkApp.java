package t91;

import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroWriters;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.myorg.User;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import java.util.Arrays;

public class AvroFileSinkApp {

    private static final String OUTPUT_PATH = "./il/";
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.setParallelism(4);
        OutputFileConfig config = OutputFileConfig
                .builder()
                .withPartPrefix("il")
                .withPartSuffix(".avro")
                .build();

        DataStream<User> source = env.fromCollection(Arrays.asList(getUser(), getUser(), getUser(), getUser(), getUser(), getUser()));
        source.sinkTo(FileSink.forBulkFormat(new Path(OUTPUT_PATH), AvroWriters.forSpecificRecord(User.class)).withBucketCheckInterval(5000).withRollingPolicy(OnCheckpointRollingPolicy.build())
                .withOutputFileConfig(config).withBucketAssigner(new DateTimeBucketAssigner<>("yyyy/MM/dd/HH")).build());
        env.execute("FileSinkProgram");
        Thread.sleep(300000);
    }

    public static User getUser() {
        User u = new User();
        u.setId(1L);
        u.setName("raj");
        return u;
    }
}