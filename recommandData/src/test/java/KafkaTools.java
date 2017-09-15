import com.wonder.util.ResourcesManager;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

/**
 * Created by Administrator on 2017/8/21.
 */
public class KafkaTools {

    private static final Properties props;
    private static KafkaProducer<String, String> producer;

    static {
        props = new Properties();
        props.put("serializer.class", ResourcesManager.getProp("kafka.serializer"));
        props.put("bootstrap.servers", ResourcesManager.getProp("kafka.bootstrap.servers"));
        props.put("acks", ResourcesManager.getProp("kafka.acks"));
        props.put("retries", ResourcesManager.getProp("kafka.retries"));
        props.put("batch.size", ResourcesManager.getProp("kafka.batch.size"));
        props.put("linger.ms", ResourcesManager.getProp("kafka.linger.ms"));
        props.put("request.timeout.ms", ResourcesManager.getProp("kafka.request.timeout.ms"));
        props.put("metadata.fetch.timeout.ms", ResourcesManager.getProp("kafka.metadata.fetch.timeout.ms"));
        props.put("key.serializer", ResourcesManager.getProp("kafka.key.serializer"));
        props.put("value.serializer", ResourcesManager.getProp("kafka.value.serializer"));
        addShutdownHook();
    }

    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                close();
            }
        }));
    }

    /**
     * 获取kafka连接属性
     *
     * @return
     */
    public static Producer<String, String> getKafka() {
        if (null == producer) {
            producer = new KafkaProducer<String, String>(props);
        }
        return producer;
    }


    /**
     * 关闭当前生产者
     */
    public static void close() {
        if (null != producer) {
            producer.close();
        }
    }

    public static void main(String[] args) {
        System.out.println(getKafka());
    }

}
