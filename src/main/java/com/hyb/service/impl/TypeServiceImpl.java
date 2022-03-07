package com.hyb.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import com.hyb.mapper.TypeMapper;
import com.hyb.pojo.Type;
import com.hyb.service.TypeService;
import com.hyb.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Type selectOne(Long id) {
        String key = "type";
        //首先查缓存
        Type type = (Type) redisUtils.hGet(key, String.valueOf(id));

        if (type == null) {
            type = typeMapper.selectOneById(id);
            redisUtils.hSet(key, String.valueOf(id), type);
        }

        return type;
    }

    @Override
    public Type selectOne(String name) {
        return typeMapper.selectOneByName(name);
    }

    /**
     * 仅仅查询出所有的type
     * @return
     */
    @Override
    public List<Type> selectList() {
        String key = "type";
        List<Type> types = new ArrayList<>();
        boolean exists = redisUtils.exists(key);
        if (!exists) {
            types = typeMapper.selectList();
            for (Type type : types) {
                redisUtils.hSet(key, String.valueOf(type.getId()), type);
            }
        } else {
            Map<Object, Object> map = redisUtils.hGetAll(key);
            Set<Map.Entry<Object, Object>> entries = map.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                types.add((Type) entry.getValue());
            }
        }

        return types;
    }

    /**
     * 排行榜 ranking
     * 拿到types中 blog数量最多的size个type  排序由redis的zset完成
     * 缓存可以采用zset  每个type的blog size作为score  type id作为value
     * @param size
     * @return
     */
    @Override
    public Map<Type, Integer> selectList(int size) {
        String key = "type_blogs";
        Map<Type, Integer> map = new LinkedHashMap<>();
        List<Type> types;
        boolean exists = redisUtils.exists(key);
        if (!exists) {
            types = typeMapper.selectListAndBlog();  //未排序
            for (Type type : types) {
                redisUtils.zAdd(key, type.getId(), (double) type.getBlogs().size());  //放入zset中
            }
        }

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisUtils.zRevRangeWithScores(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        if (size >= typedTuples.size()) {
            size = typedTuples.size();
        }

        int i = 0;
        for (ZSetOperations.TypedTuple<Object> typedTuple: typedTuples) {
            if (i >= size) {
                break;
            }
            Object typeId =  typedTuple.getValue();
            Integer score = typedTuple.getScore().intValue(); //数量
            exists = redisUtils.exists("type");
            if (!exists) {
                this.selectList();
            }
            Type type = (Type) redisUtils.hGet("type", String.valueOf(typeId));
            map.put(type, score);
            i++;
        }

        return map;
    }

    /**
     * 分页缓存
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Type> selectList(int page, int size) {
        String key = "type";
        List<Type> types;
        List<Object> list = redisUtils.getPage(key, page, size);
        if (list == null || list.size() == 0) {   //分页缓存为空
            types = typeMapper.selectList();
            //放入缓存
            for (Type type : types) {
                redisUtils.setPage(key, String.valueOf(type.getId()), type.getId().doubleValue(), type);
            }
        } else {
            types = new ArrayList<>();
            for (Object obj : list) {
                types.add((Type) obj);
            }
        }
        Page<Type> typePage = new Page<>();
        typePage.setPageNum(page);
        typePage.setPageSize(size);
        typePage.setTotal(redisUtils.getPageSize(key));
        PageInfo<Type> pageInfo = new PageInfo<>(typePage);
        pageInfo.setList(types);
        return pageInfo;
    }

    @Override
    public Type insert(Type type) {
        //首先判断数据库中有无这个数据
        Type t = typeMapper.selectOneByName(type.getName());
        if (t != null) {
            return null;
        }
        typeMapper.insert(type);
        type = this.selectOne(type.getName());
        //放入缓存
        redisUtils.setPage("type", String.valueOf(type.getId()), type.getId().doubleValue(), type);
        return type;
    }

    @Override
    public Type update(Long id, Type type) {
        Type t = typeMapper.selectOneById(id);
        if (t == null) {
            return null;
        }
        typeMapper.update(type);
        //更新缓存
        redisUtils.hSet("type", String.valueOf(type.getId()), type);
        return type;
    }

    @Override
    public void delete(Long id) {
        boolean flag = true;  //是否可以删除
        //首先需要先判断是否有博客是这个类型
        Map<Type, Integer> map = this.selectList(10000000);

        for (Type type : map.keySet()) {
            if (type.getId().equals(id)) { //Long类型比较大小用equals
                flag = false;
            }
        }

        if (flag) {
            //如果没有，才能删除
            typeMapper.delete(id);
            redisUtils.delPage("type", String.valueOf(id)); // redisUtils.hDel("type", String.valueOf(id)); 已经在delPage中执行了
        } else {
            throw new RuntimeException("该类型下还有所属博客！！！");
        }

    }

}
