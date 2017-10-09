package com.wonder.service;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/8/21.
 */
public interface GetRecommendResultService {

    /**
     * 根据用户名获去得分最高的栏目(从redis)
     * @param userid
     * @return
     */
    public String getItemByUserID(String userid) throws JSONException;

}
