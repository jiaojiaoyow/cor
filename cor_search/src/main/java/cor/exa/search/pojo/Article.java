package cor.exa.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

/**
 * 文章实体类
 *
 */
@Data
@Document(indexName = "tensquare", type = "article")
public class Article implements Serializable {

    @Id
    private String id;

    //@Field注解用于指定使用的分词器
    @Field(index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;

    @Field(index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    //审核状态
    private String state;


}