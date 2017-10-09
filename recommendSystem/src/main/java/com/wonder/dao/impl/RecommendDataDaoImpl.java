package com.wonder.dao.impl;

import com.wonder.dao.RecommendDataDao;
import org.bson.Document;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import static com.wonder.util.ToolUtil.dbInsert2Redis;
import static com.wonder.util.ToolUtil.getDbDoc;
import static com.wonder.util.ToolUtil.getRecommandItem;


/**
 * Created by Administrator on 2017/8/21.
 */
@Service(value = "recommendDataDao")
public class RecommendDataDaoImpl implements RecommendDataDao {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RecommendDataDaoImpl.class
            .getName());


    public void translate2Kafka(String message) {
//        KafkaProducers.sendMsg(message);
    }


    public String resultItem(String userid) throws JSONException {
        String getItem = null;  //配置默认返回结果，从redis和数据库都得不到结果
        getItem = getRecommandItem(userid);   //先从redis中拿最高得分的栏目，如果没有则从db中得到
        //如果从redis中得不到则从数据库中得到数据
        if (null == getItem || "".equalsIgnoreCase(getItem)) {
            log.info("从redis中得不到推荐的栏目或标签，准备从数据库查询");
            Document document = getDbDoc(userid);
            log.info("从数据库的得到的文档:"+document);
            if(!"null".equalsIgnoreCase(String.valueOf(document))) {
                dbInsert2Redis(document, userid);
                getItem = getRecommandItem(userid);
            }else {
                getItem = null;
            }

        }
        return getItem;
    }

    @Override
    public Document queryAllItem(String userid) {
        Document document = getDbDoc(userid);
        return document;
    }
}
