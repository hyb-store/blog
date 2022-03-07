package com.hyb.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.hyb.mapper.BlogMapper;
import com.hyb.pojo.Blog;
import com.hyb.service.BlogService;
import com.hyb.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public Blog selectOne(Long id) {
        //首先查缓存
        String key = "blog";
        boolean exists = redisUtils.exists(key);
        if (!exists) {
            Blog blog = blogMapper.selectOneById(id);
            redisUtils.hSet(key, String.valueOf(id), blog);
        }
        return (Blog) redisUtils.hGet(key, String.valueOf(id));
    }

    @Override
    public Blog selectOne(String title) {
        return blogMapper.selectOneByTitle(title);
    }

    /**
     * ranking
     * @param size
     * @return
     */
    @Override
    public List<Blog> selectList(int size) {
        return blogMapper.selectListByUpdateTime(size);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size) {
        String key = "blog";
        List<Blog> blogs;
        boolean exists = redisUtils.exists(key);

        if (!exists) {
            blogs = blogMapper.selectList();
            for (Blog blog : blogs) {
                redisUtils.hSet(key, String.valueOf(blog.getId()), blog);
            }
        } else {
            List<Object> list = redisUtils.getPage(key, page, size);
            blogs = new ArrayList<>();
            for (Object blog : list) {
                blogs.add((Blog) blog);
            }
        }

        return getPageInfo(page, size, blogs, key);
    }

    public PageInfo<Blog> selectList(int page, int size, boolean published) {
        String key = "blog";

        List<Blog> publishedList = new ArrayList<>();
        List<Object> list = redisUtils.getPage(key, page, size);

        if (list == null || list.size() == 0) {
            List<Blog> blogs = blogMapper.selectList();
            for (Blog blog : blogs) {
                redisUtils.setPage(key, String.valueOf(blog.getId()), blog.getId().doubleValue(), blog);
            }
        }

        list = redisUtils.getPage(key, page, size);
        //将所有已发布的blog封装到publishedList中
        for (Object o : list) {
            Blog blog = (Blog) o;
            if (blog.isPublished()) {
                publishedList.add(blog);
            }
        }
        return this.getPageInfo(page, size, publishedList, key);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size, String query) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectListConditional(query);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size, String title, Long typeId, boolean recommend) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectListMultipleConditional(title, typeId, recommend);
        return new PageInfo<>(list);
    }

    /**
     * 归档
     * @return
     */
    public Map<String, List<Blog>> selectMap() {
        Map<String, List<Blog>> map = new LinkedHashMap<>();  //改成LinkedHashMap  排序的hashmap
        //首先查询出所有的年份 create_time
        List<String> years = blogMapper.selectListYear();

        //然后封装一个 map<年份，blogs>
        for (String year : years)  {
            List<Blog> list = blogMapper.selectListByYear(year);
            map.put(year, list);
        }

        return map;
    }

    @Override
    public Blog update(Long id, Blog blog) {
        String key = "blog";
        Blog b = blogMapper.selectOneById(id);
        if (b == null) {
            return null;
        }
        blog.setUpdateTime(new Date());
        blogMapper.update(blog);
        blog = blogMapper.selectOneById(id);
        redisUtils.hSet(key, String.valueOf(blog.getId()), blog);
        return blog;
    }

    @Override
    public void incrView(Long id, Blog blog) {
        blog.setViews(blog.getViews() + 1);
        blogMapper.updateViews(id);
        //更新缓存
        redisUtils.hSet("blog", String.valueOf(id), blog);
    }

    @Override
    public Blog insert(Blog blog) {
        Blog b = blogMapper.selectOneByTitle(blog.getTitle());
        if (b != null) {
            return null;
        }
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());
        blog.setViews(0);
        blogMapper.insert(blog);

        String key = "type_blogs";
        redisUtils.setPage("blog", String.valueOf(blog.getId()), blog.getId().doubleValue(), blog);
        boolean exist = redisUtils.exists(key);
        if (exist) {
            redisUtils.zIncrby(key, blog.getType().getId());
        }
        return blog;
    }

    @Override
    public void delete(Long id) {
        blogMapper.delete(id);
        redisUtils.delPage("blog", String.valueOf(id));
        redisUtils.zIncrby("type_blogs", id);
    }

    private PageInfo<Blog> getPageInfo(int page, int size, List<Blog> list, String key) {
        Page<Blog> blogPage = new Page<>();
        blogPage.setPageNum(page);
        blogPage.setPageSize(size);
        blogPage.setTotal(redisUtils.getPageSize(key));

        PageInfo<Blog> pageInfo = new PageInfo<>(blogPage);
        pageInfo.setList(list);

        return pageInfo;
    }

}
