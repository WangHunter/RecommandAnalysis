package com.wonder.service;

import org.bson.Document;

/**
 * Created by Administrator on 2017/9/29.
 */
public interface QueryAllItemByUseridService {

    public Document queryItemByUserid(String userid);
}
