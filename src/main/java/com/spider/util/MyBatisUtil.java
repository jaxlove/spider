package com.spider.util;

import com.spider.meituan.dao.ShopMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/19 12:01
 */
public class MyBatisUtil {
    private static MapperHelper mapperHelper = new MapperHelper();

    static {
        Config config = new Config();
        config.setNotEmpty(false);
        mapperHelper.setConfig(config);
        mapperHelper.registerMapper(ShopMapper.class);
    }

    public static <T> T getMapper(Class<T> class_) {
        SqlSession sqlSession = null;
        try {
            sqlSession = getSeesion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //配置完成后，执行下面的操作
        mapperHelper.processConfiguration(sqlSession.getConfiguration());
        //获取Mapper
        return sqlSession.getMapper(class_);
    }

    public static SqlSession getSeesion() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession(true);
        return session;
    }

}
