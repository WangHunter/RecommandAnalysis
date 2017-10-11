package com.wonder.data;

import com.wonder.util.KafkaConsumer;
import com.wonder.util.ResourcesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Administrator on 2017/8/22.
 */

public class DataDeal {
    private static final Logger log = LogManager.getLogger(DataDeal.class.getName());
    private static String zookeeper = ResourcesManager.getProp("kafka.zookeeper.quorum");
    private static String groupId = ResourcesManager.getProp("kafka.groupId");
    private static String topic = ResourcesManager.getProp("kafka.topic");
    private static int threads = Integer.valueOf(ResourcesManager.getProp("kafka.consumer.threadNum"));

    public static void main(String[] args) throws InterruptedException {
        log.info("开始消费数据");
        KafkaConsumer kafkaConsumer = new KafkaConsumer(topic,threads,zookeeper,groupId);
        new Thread(kafkaConsumer).start();
        Thread.sleep(30*60*1000);  //30分钟后结束
        kafkaConsumer.shutdown();

    }
}
