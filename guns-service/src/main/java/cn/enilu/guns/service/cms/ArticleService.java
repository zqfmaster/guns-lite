package cn.enilu.guns.service.cms;

import cn.enilu.guns.bean.entity.cms.Article;
import cn.enilu.guns.bean.enumeration.cms.ChannelEnum;
import cn.enilu.guns.dao.cms.ArticleRepository;
import cn.enilu.guns.utils.Maps;
import cn.enilu.guns.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * 查询首页最近5条资讯数据
     *
     * @return
     */
    public List<Article> queryIndexNews() {
        cn.enilu.guns.utils.factory.Page<Article> page = query(1, 5, ChannelEnum.NEWS.getId());
        return page.getRecords();
    }


    public cn.enilu.guns.utils.factory.Page findPage(cn.enilu.guns.utils.factory.Page<Article> page, final Map<String, String> params) {
        Pageable pageable = null;
        if (page.isOpenSort()) {
            pageable = new PageRequest(page.getCurrent() - 1, page.getSize(), page.isAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, page.getOrderByField());
        } else {
            pageable = new PageRequest(page.getCurrent() - 1, page.getSize());
        }

        org.springframework.data.domain.Page<Article> pageResult = articleRepository.findAll(new Specification<Article>() {


            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(params.get("title"))) {
                    list.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + params.get("title") + "%"));
                }
                if (StringUtils.isNotEmpty(params.get("idChannel"))) {
                    list.add(criteriaBuilder.equal(root.get("idChannel").as(Long.class), Long.valueOf(params.get("idChannel").toString())));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        page.setTotal(Integer.valueOf(pageResult.getTotalElements() + ""));
        page.setRecords(pageResult.getContent());
        return page;
    }

    public cn.enilu.guns.utils.factory.Page<Article> query(int currentPage, int size, Long idChannel) {
        cn.enilu.guns.utils.factory.Page page = new cn.enilu.guns.utils.factory.Page(currentPage, size, "id");
        Map params = Maps.newHashMap("idChannel", String.valueOf(idChannel));
        return findPage(page, params);

    }
}
