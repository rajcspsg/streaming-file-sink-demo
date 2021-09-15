package t_5_9_10_113;

public class Rate {
    public Rate() {};

    public Rate(Integer id, Long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public Integer id;
    public Long timestamp;

    @Override
    public int hashCode() {return (int) this.id.hashCode();}

    @Override
    public boolean equals(Object other) {
        return other instanceof Rate && this.id.equals(((Rate) other).id) && this.timestamp.equals(((Rate) other).timestamp);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }
}
