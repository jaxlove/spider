package com.spider.dazhong;

import com.spider.dazhong.dao.ShopMapper;
import com.spider.dazhong.entity.Shop;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangdejun
 * @description: TODO description
 * @date 2019/10/15 21:27
 */
public class MyBatisTest {
    public static void main(String[] args) throws IOException {
        commonMapper();
    }

    public static void commonMapper() throws IOException {
        MapperHelper mapperHelper = new MapperHelper();
//特殊配置
        Config config = new Config();
        config.setNotEmpty(false);
//设置配置
        mapperHelper.setConfig(config);
// 注册自己项目中使用的通用Mapper接口，这里没有默认值，必须手动注册
        mapperHelper.registerMapper(ShopMapper.class);
            SqlSession sqlSession = getSeesion();
            //配置完成后，执行下面的操作
            mapperHelper.processConfiguration(sqlSession.getConfiguration());
            try {
                //获取Mapper
                ShopMapper mapper = sqlSession.getMapper(ShopMapper.class);
            Shop shop = new Shop();
            shop.setmShopId("test");
            mapper.insert(shop);
        } finally {
            sqlSession.close();
        }

    }

    public static SqlSession getSeesion() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession session = sqlSessionFactory.openSession(true);
        return session;
    }
}
