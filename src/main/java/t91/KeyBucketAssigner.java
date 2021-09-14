package t91;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;

/** Use first field for buckets. */
public final class KeyBucketAssigner
        implements BucketAssigner<Tuple2<Integer, Integer>, String> {

    private static final long serialVersionUID = 987325769970523326L;

    @Override
    public String getBucketId(final Tuple2<Integer, Integer> element, final Context context) {
        return String.valueOf(element.f0);
    }

    @Override
    public SimpleVersionedSerializer<String> getSerializer() {
        return SimpleVersionedStringSerializer.INSTANCE;
    }
}
