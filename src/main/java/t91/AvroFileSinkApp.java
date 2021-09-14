package t91;

import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroWriters;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.myorg.User;
import java.util.Arrays;

public class AvroFileSinkApp {

    private static final String OUTPUT_PATH = "./il/";
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(4);
        env.enableCheckpointing(5000L);

        DataStream<User> source = env.fromCollection(Arrays.asList(getUser()));
        FileSink<User> sink = org.apache.flink.connector.file.sink.FileSink.forBulkFormat(new Path(OUTPUT_PATH), AvroWriters.forSpecificRecord(User.class)).build();

        source.addSink( sink);
        env.execute("FileSinkProgram");
    }

    public static User getUser() {
        User u = new User();
        u.setId(1L);
        return u;
    }
}