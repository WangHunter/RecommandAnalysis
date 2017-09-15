/**
 * Created by Administrator on 2017/8/21.
 */

import com.wonder.util.ResourcesManager;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducers {

    private static final String TOPIC = ResourcesManager.getProp("kafka.topic"); //kafka创建的topic

    public static void sendMsg(String msg) {
            Producer producer =  new KafkaTools().getKafka();
            ProducerRecord message =
                    new ProducerRecord(TOPIC, msg);
//        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>(100);
//        messages.add(message);
        producer.send(message);
    }

    public static void main(String[] args) {
        String str = "{\"actionType\":\"02\",\"contentId\":\"23544450\",\"newsTag\":\"\",\"nodeId\":\"263910\",\"userId\":\"000005\"}";
        sendMsg(str);
    }
}
