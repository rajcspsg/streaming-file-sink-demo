package t_5_9_10_113;

import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;

public class KeyBucketAssigner implements BucketAssigner<Rate, String> {
    @Override
    public String getBucketId(Rate element, Context context) {
        return String.valueOf(element.id % 10);
    }

    @Override
    public SimpleVersionedSerializer<String> getSerializer() {
        return SimpleVersionedStringSerializer.INSTANCE;
    }
}
