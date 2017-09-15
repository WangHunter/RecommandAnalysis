package com.wonder.dao.impl;

import com.wonder.dao.RecommandDataDao;
import org.bson.Document;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import static com.wonder.util.ToolUtil.*;

/**
 * Created by Administrator on 2017/8/21.
 */
@Service(value = "recommandDataDao")
public class RecommandDataDaoImpl implements RecommandDataDao {

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(RecommandDataDaoImpl.class
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
            if(!"null".equalsIgnoreCase(String.valueOf(document))) {
                dbInsert2Redis(document, userid);
                getItem = getRecommandItem(userid);
            }
        }
        return getItem;
    }

}
