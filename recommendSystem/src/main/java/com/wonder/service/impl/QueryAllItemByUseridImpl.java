package com.wonder.service.impl;

import com.wonder.dao.RecommendDataDao;
import com.wonder.service.QueryAllItemByUseridService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/9/29.
 */
@Service(value = "queryAllItemByUseridService")
public class QueryAllItemByUseridImpl implements QueryAllItemByUseridService {
    private static final Logger log = LogManager.getLogger(QueryAllItemByUseridImpl.class
            .getName());

    @Autowired
    private RecommendDataDao recommendDataDao;


    @Override
    public Document queryItemByUserid(String userid) {
        Document doc = recommendDataDao.queryAllItem(userid);
        return doc;
    }
}
