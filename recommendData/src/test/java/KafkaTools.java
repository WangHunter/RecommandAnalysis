import com.mongodb.client.MongoCollection;
import com.wonder.util.MongoDBUtil;
import com.wonder.util.ResourcesManager;
import kafka.utils.Utils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.bson.Document;

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


        /**
         * mongodb测试
         */
        String dbName = ResourcesManager.getProp("mongodb.db");
        String collName = ResourcesManager.getProp("mongodb.coll");
        MongoCollection<Document> coll = MongoDBUtil.instance.getCollection(dbName, collName);

        // 插入多条
//         for (int i = 1; i <= 4; i++) {
//         Document doc = new Document();
//         doc.put("_id", i);
//         doc.put("school", "NEFU" + i);
//         Document interests = new Document();
//         interests.put("game", "game" + i);
//         interests.put("ball", "ball" + i);
//         doc.put("interests", interests);
//         coll.insertOne(doc);
//         }

        // // 根据ID查询
//         String id = "1";
//         Document doc = MongoDBUtil.instance.findById(coll, id);
//         System.out.println(doc);

        // 查询多个
        // MongoCursor<Document> cursor1 = coll.find(Filters.eq("name", "zhoulf")).iterator();
        // while (cursor1.hasNext()) {
        // org.bson.Document _doc = (Document) cursor1.next();
        // System.out.println(_doc.toString());
        // }
        // cursor1.close();

        // 查询多个
        // MongoCursor<Person> cursor2 = coll.find(Person.class).iterator();

        // 修改数据
//         String id = "1";
//         Document newdoc = new Document();
//         newdoc.put("school", "HPU");
//         MongoDBUtil.instance.updateById(coll, id, newdoc);

        // 统计表
        // System.out.println(MongoDBUtil.instance.getCount(coll));

        // 查询所有
//        Bson filter = Filters.eq("count", 0);
//        MongoDBUtil.instance.find(coll, filter);


        /**
         * 分区
         */
        String key = "2001731";
        System.out.println(Utils.abs(key.hashCode()%3));
    }

}
