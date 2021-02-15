package com.qingshan.seckill.dao;

import com.qingshan.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillMapperTest {

    @Resource
    private SeckillMapper seckillMapper;

    @Test
    public void reduceNumber() {
        long seckillId = 1001;
        Timestamp nowTime = Timestamp.from(Instant.now());
        int i = seckillMapper.reduceNumber(seckillId, nowTime);
        System.out.println(i);
    }

    @Test
    public void queryById() {
        long seckillId = 1000;
        Seckill seckill = seckillMapper.queryById(seckillId);
        System.out.println(seckill.toString());
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillMapper.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill.toString());
        }
    }
}