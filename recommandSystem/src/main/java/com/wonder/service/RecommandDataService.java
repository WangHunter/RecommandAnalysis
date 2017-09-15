package com.wonder.service;

/**
 * Created by Administrator on 2017/8/21.
 */
public interface RecommandDataService {

    /**
     *发送用户行为信息到kafka
     */
    public void translateData(String messages);
}
