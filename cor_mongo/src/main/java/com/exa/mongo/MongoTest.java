package com.exa.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BasicBSONObject;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;


public class MongoTest {
    public static void main(String[] args) {
        //连接mongo服务
        MongoClient mongoClient=new MongoClient("101.37.149.133");

        //得到要操作的数据库
        MongoDatabase spitdb=mongoClient.getDatabase("spitdb");

        //得到要操作的集合
        MongoCollection<Document> spit=spitdb.getCollection("spit");

        /**
         * 查询
         */

        //得到集合中的所有文档
        FindIterable<Document> documents=spit.find();
//        //1. 封装查询条件
//        BasicDBObject bson =new BasicDBObject("userid","1013");//查用户id为1013
//        BasicDBObject bson1 =new BasicDBObject("visits",new BasicDBObject("&gt",1000));//查询访问量大于1000
//        //2. 得到集合中相关文档
//        FindIterable<Document> documents1=spit.find(bson);
//
        //遍历数据(注意每一列的类型要一致)
        for(Document document:documents ){
            System.out.println("内容："+document.getString("userid"));
        }

//
//        /**
//         * 添加数据
//         */
//        Map<String,Object> map=new HashMap<>();
//        map.put("content","111");
//        map.put("userid","1016");
//        map.put("visits",100);
//        Document document=new Document();
//        spit.insertOne(document);


        mongoClient.close();

    }
}
