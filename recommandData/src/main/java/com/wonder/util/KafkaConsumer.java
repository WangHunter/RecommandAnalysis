package com.wonder.util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.wonder.util.ToolUtil.*;

/**
 * Created by Administrator on 2017/8/22.
 */

/**
 * 自定义简单Kafka消费者， 使用高级API
 * Created by gerry on 12/21.
 */
public class KafkaConsumer implements Runnable {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(KafkaConsumer.class.getName());
    private static String dbName = ResourcesManager.getProp("mongodb.db");
    private static String collName = ResourcesManager.getProp("mongodb.coll");
    private static MongoCollection<Document> coll = MongoDBUtil.instance.getCollection(dbName, collName);

    private ConsumerConnector consumer;
    private String topic;
    private int numThreads;
    private ExecutorService executorPool;

    /**
     * 构造函数
     *
     * @param topic      Kafka消息Topic主题
     * @param numThreads 处理数据的线程数/可以理解为Topic的分区数
     * @param zookeeper  Kafka的Zookeeper连接字符串
     * @param groupId    该消费者所属group ID的值
     */
    public KafkaConsumer(String topic, int numThreads, String zookeeper, String groupId) {
        // 1. 创建Kafka连接器
        this.consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
        // 2. 数据赋值
        this.topic = topic;
        this.numThreads = numThreads;
    }

    public void run() {
        // 1. 指定Topic
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(this.topic, this.numThreads);
        // 2. 指定数据的解码器
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        // 3. 获取连接数据的迭代器对象集合
        /**
         * Key: Topic主题
         * Value: 对应Topic的数据流读取器，大小是topicCountMap中指定的topic大小
         */
        Map<String, List<KafkaStream<String, String>>> consumerMap = this.consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

        // 4. 从返回结果中获取对应topic的数据流处理器
        List<KafkaStream<String, String>> streams = consumerMap.get(this.topic);

        // 5. 创建线程池
        this.executorPool = Executors.newFixedThreadPool(this.numThreads);
        // 6. 构建数据输出对象
        int threadNumber = 0;
        for (final KafkaStream<String, String> stream : streams) {
            this.executorPool.submit(new ConsumerKafkaStreamProcesser(stream,threadNumber));
            threadNumber++;
        }

//        Iterator<KafkaStream<String, String>> iterator = streams.iterator();
//        while (iterator.hasNext()){
//            KafkaStream<String, String> next = iterator.next();
//            this.executorPool.submit(new ConsumerKafkaStreamProcesser(next, threadNumber));
//            threadNumber++;
//        }

    }


    /**
     * 根据传入的zk的连接信息和groupID的值创建对应的ConsumerConfig对象
     *
     * @param zookeeper zk的连接信息，
     * @param groupId   该kafka consumer所属的group id的值， group id值一样的kafka consumer会进行负载均衡
     * @return Kafka连接信息
     */
    private ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
        Properties prop = new Properties();
        prop.put("group.id", groupId); // 指定分组id
        prop.put("zookeeper.connect", zookeeper); // 指定zk的连接url
        prop.put("zookeeper.session.timeout.ms", "400"); //
        prop.put("zookeeper.sync.time.ms", "200");
        prop.put("auto.commit.interval.ms", "1000");
//        prop.put("consumer.timeout.ms","1");
//        prop.put("auto.offset.reset", "smallest");  //更换group后，从topic的开始位置消费所有消息,smallest,largest
        prop.put("auto.commit.enable","true");   //定期提交offset
        // 3. 构建ConsumerConfig对象
        return new ConsumerConfig(prop);
    }


    /**
     * Kafka消费者数据处理线程
     */
    public static class ConsumerKafkaStreamProcesser implements Runnable {
        // Kafka数据流
        private KafkaStream<String, String> stream;
        // 线程ID编号
        private int threadNumber;

        public ConsumerKafkaStreamProcesser(KafkaStream<String, String> stream,int threadNumber) {
            this.stream = stream;
            this.threadNumber = threadNumber;
        }

        public void run() {
            log.info("准备读取kafka数据");
            Jedis jedis = RedisUtil.getJedis();
            // 1. 获取数据迭代器
            ConsumerIterator<String, String> iter = this.stream.iterator();
            // 2. 迭代输出数据
            while (iter.hasNext()) {
                // 2.1 获取数据值
                MessageAndMetadata value = iter.next();
                String getMessage = value.message().toString();
                log.info("从kafka得到的信息为:"+getMessage);

                // 2.2 输出到redis
                String key = null;
                String item = null;
                Double score = 0.0;

                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(getMessage.toString());
                    Iterator it = jsonObj.keys();
                    while (it.hasNext()) {
                        String next = (String) it.next();
                        if ("userId".equalsIgnoreCase(next)) {
                            key = (String) jsonObj.get("userId");
                        }
                        if ("newsTag".equalsIgnoreCase(next)) {
                            item = (String) jsonObj.get("newsTag");
                        }
                        if ("actionType".equalsIgnoreCase(next)) {
                            score = Double.parseDouble("".equals(getScore( (String) jsonObj.get("actionType")))?"0.0":getScore( (String) jsonObj.get("actionType")));
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    continue;
                }
                log.info("用户的相关信息:" + key + "," + item + "," + score);
                //存在更新数据
                if (jedis.exists(key)) {
                    log.info("可以从redis中查到key:" + key);
                    //存在则更新redis数据，再同步到数据库
                    jedis.zincrby(key, score, item);

                    //根据此key查找第一的栏目，用来判断是否需要更新时间以及做减分操作
                    String getRecommandItem = null;
                    Set sets = jedis.zrevrangeByScore(key, "+inf", "-inf", 0, 2);
                    Iterator<String> itSets = sets.iterator();
                    while (itSets.hasNext()) {
                        String firstItem = itSets.next();
                        if (!"time".equalsIgnoreCase(firstItem)) {    //用来判断成员是否是time
                            getRecommandItem = firstItem;
                            break;
                        }
                    }

                    if (item.equalsIgnoreCase(getRecommandItem)) {
                        String noedays = currentTime();
                        int getRecommandTime = jedis.zscore(key, "time").intValue();  //double转int
                        try {
                            long timeLag = getTimelag(String.valueOf(getRecommandTime), noedays);
                            if (timeLag >= 1) {
                                double reduceScores = -timeLag * 0.5;  //此处应是负分数
                                jedis.zincrby(key, reduceScores, item);   //对item减分
                                jedis.zadd(key, Integer.valueOf(noedays), "time");  //重新更新time时间
                            }
                        } catch (ParseException e) {
                            log.error(e.getStackTrace());         //后期要异常处理
                        }
                    }
                    deleteDb("userid", key);
                    redisInsert2Db(key);
                }

                //不存在添加数据
                if (!jedis.exists(key)) {
                    Document doc = null;
                    log.info("不能从redis中查到key:" + key);
                    //从数据库查找是否存在，存在则更新数据库信息，再同步到redis
                    BasicDBObject searchQuery = new BasicDBObject();
                    searchQuery.put("userid", key);
                    doc = coll.find(searchQuery).first();
//                    System.out.println("++++"+cursor.first());
//                    Document doc = coll.find(Filters.eq("userid",key)).first();
                    log.info("从数据库根据userid查到的数据为:" + doc);

                    //数据库中存在
                    if (!"null".equalsIgnoreCase(String.valueOf(doc))) {
                        log.info("能从数据库中得到doc");
                        Document newdoc = new Document();
                        long oldScore = Integer.valueOf(doc.get(item).toString());
                        newdoc.put(item, String.valueOf(oldScore + score));    //更新得分
                        updateById("userid", key, newdoc);
                        //同步数据到redis
                        Document document = getDbDoc(key);
                        dbInsert2Redis(document, key);
                    }

                    if ("null".equalsIgnoreCase(String.valueOf(doc))) {
                        log.info("不能从数据库中得到doc");
                        //添加判断，数据库数据不存在，则添加数据，再同步到数据库
                        jedis.zadd(key, score, item);
                        jedis.zadd(key, Integer.valueOf(currentTime()), "time");     //没有数据则添加第一条记录的时间
                        redisInsert2Db(key);
                    }
                }

            }
            // 3. 表示当前线程执行完成
            log.info("Shutdown Thread:" + this.threadNumber);
            jedis.close();
        }

    }


    public synchronized  void shutdown() throws InterruptedException {
        // 1. 关闭和Kafka的连接，这样会导致stream.hashNext返回false
        if (this.consumer != null) {
            wait(2000);  //等待offset提交完成关闭consumer
            this.consumer.shutdown();
        }

        // 2. 关闭线程池，会等待线程的执行完成
        if (this.executorPool != null) {
            // 2.1 关闭线程池
            this.executorPool.shutdown();

            // 2.2. 等待关闭完成, 等待五秒
            try {
                if (!this.executorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly!!");
                }
            } catch (InterruptedException e) {
                log.error(e.getStackTrace());
            }
        }

    }


    public static void main(String[] args) {
//        deleteDb("userid","123456789");
//        Document newdoc = new Document();
//        newdoc.put("item111", "HPU");
//        updateById( "userid","223456789", newdoc);

//        Document getDoc = getDbDoc("223456789");
//        System.out.println(getDoc.toJson());
    }

}
