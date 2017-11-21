package com.wonder.service.impl;

import com.wonder.dao.RecommendDataDao;
import com.wonder.service.GetRecommendResultService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/8/21.
 */
@Service(value = "getRecommendResultService")
public class GetRecommendResuleImpl implements GetRecommendResultService {

    private static final Logger log = LogManager.getLogger(GetRecommendResuleImpl.class
            .getName());

    @Autowired
    private RecommendDataDao recommendDataDao;

    public String getItemByUserID(String userid) throws JSONException {
        String result = recommendDataDao.resultItem(userid);
        if (null == result || "null".equalsIgnoreCase(result)) {
            log.info("根据此用户id:"+userid+",得不到推荐结果");
            result = "";   //如果获取不到推荐栏目，给默认的栏目
        }
        return result;
    }
}
