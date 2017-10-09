package com.wonder.dao;

import org.bson.Document;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/8/21.
 */
public interface RecommendDataDao {

    public void translate2Kafka(String message);

    public String resultItem(String userid) throws JSONException;

    public Document queryAllItem(String userid);
}
