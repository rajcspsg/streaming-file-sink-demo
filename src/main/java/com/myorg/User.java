package com.myorg;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.AvroGenerated;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

@AvroGenerated
public class User extends SpecificRecordBase implements SpecificRecord {
    private static final long serialVersionUID = 8699049231783654635L;
    public static final Schema SCHEMA$ = (new Parser()).parse("{\"type\":\"record\",\"name\":\"User\",\"namespace\":\"com.myorg\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
    private static SpecificData MODEL$ = new SpecificData();
    private static final BinaryMessageEncoder<User> ENCODER;
    private static final BinaryMessageDecoder<User> DECODER;
    /** @deprecated */
    @Deprecated
    public long id;
    /** @deprecated */
    @Deprecated
    public String name;
    private static final DatumWriter<User> WRITER$;
    private static final DatumReader<User> READER$;

    public static Schema getClassSchema() {
        return SCHEMA$;
    }

    public static BinaryMessageDecoder<User> getDecoder() {
        return DECODER;
    }

    public static BinaryMessageDecoder<User> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder(MODEL$, SCHEMA$, resolver);
    }

    public ByteBuffer toByteBuffer() throws IOException {
        return ENCODER.encode(this);
    }

    public static User fromByteBuffer(ByteBuffer b) throws IOException {
        return (User)DECODER.decode(b);
    }

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Schema getSchema() {
        return SCHEMA$;
    }

    public Object get(int field$) {
        switch(field$) {
            case 0:
                return this.id;
            case 1:
                return this.name;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public void put(int field$, Object value$) {
        switch(field$) {
            case 0:
                this.id = (Long)value$;
                break;
            case 1:
                this.name = (String)value$;
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    public void readExternal(ObjectInput in) throws IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    static {
        ENCODER = new BinaryMessageEncoder(MODEL$, SCHEMA$);
        DECODER = new BinaryMessageDecoder(MODEL$, SCHEMA$);
        WRITER$ = MODEL$.createDatumWriter(SCHEMA$);
        READER$ = MODEL$.createDatumReader(SCHEMA$);
    }

}
