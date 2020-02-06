package cor.exa.search.service;

import cor.exa.search.dao.ArticleDao;
import cor.exa.search.pojo.Article;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import util.IdWorker;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 增加文章
     * @param article
     */
    public void add(Article article){
        article.setId(idWorker.nextId()+"");
        articleDao.save(article);
    }

    /**
     * 根据关键字查询文章列表
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public PageResult findByKeywords(String keywords, int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Article> pageList = articleDao.findByTitleOrContentLike(keywords, keywords, pageRequest);
        return new PageResult(pageList.getTotalElements(), pageList.getContent());
    }


}
