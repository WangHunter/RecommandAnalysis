package com.wonder.dao;

import org.bson.Document;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/8/21.
 */
public interface RecommendDataDao {

    public void translate2Kafka(String message);

    /**
     * 得到推荐的标签
     * @param userid
     * @return
     * @throws JSONException
     */
    public String resultItem(String userid) throws JSONException;

    /**
     * 查询用户的详细标签以及得分
     * @param userid
     * @return
     */
    public Document queryAllItem(String userid);
}
