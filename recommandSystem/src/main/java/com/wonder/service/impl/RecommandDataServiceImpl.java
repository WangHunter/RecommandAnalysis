package com.wonder.service.impl;

import com.wonder.dao.RecommandDataDao;
import com.wonder.service.RecommandDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/8/21.
 */

@Service(value = "recommandDataService")
public class RecommandDataServiceImpl implements RecommandDataService {

    @Autowired
    private RecommandDataDao recommandDataDao;

    public void translateData(String messages) {

        System.out.println("1111111111111");
        //用户信息写入kafka
        recommandDataDao.translate2Kafka(messages);
    }
}
