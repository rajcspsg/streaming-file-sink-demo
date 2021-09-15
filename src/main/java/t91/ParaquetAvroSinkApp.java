package t91;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroWriters;
import org.apache.flink.formats.parquet.avro.ParquetAvroWriters;
import org.apache.flink.formats.parquet.generated.Address;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ParaquetAvroSinkApp {
    public static void main(String[] args) throws Exception {
        final File folder = Files.createDirectory(Paths.get("./pq")).toFile();

        final List<Address> data = Arrays.asList(
                new Address(1, "a", "b", "c", "12345"),
                new Address(2, "p", "q", "r", "12345"),
                new Address(3, "x", "y", "z", "12345")
        );

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(100);

        //DataStream<Address> stream = env.addSource(new FiniteTestSource<>(data), TypeInformation.of(Address.class));
        DataStream<Address> stream = env.fromCollection(data);

        stream.addSink(
                StreamingFileSink.forBulkFormat(
                                Path.fromLocalFile(folder),
                                AvroWriters.forSpecificRecord(Address.class))
                        .build());

        env.execute("test-paraquet");

    }
}
