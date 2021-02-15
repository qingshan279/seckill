package com.qingshan.seckill.service;

import com.qingshan.seckill.dto.Exposer;
import com.qingshan.seckill.dto.SeckillExecution;
import com.qingshan.seckill.entity.Seckill;
import com.qingshan.seckill.exception.RepeatKillException;
import com.qingshan.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info(seckillList.toString());
        System.out.println(seckillList.toString());
    }

    @Test
    public void getById() {
        long seckillId = 1000;
        Seckill byId = seckillService.getById(seckillId);
        System.out.println(byId.toString());
    }

    @Test
    public void exportSeckillUrl() {
        long seckillId = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        System.out.println(exposer.toString());
    }

    @Test
    public void executeSeckill() {
        long seckillId = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            long userPhone = 12222222222L;
            String md5 = "1a801434df368be0afd0d5e0b41d4b03";
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution.toString());
            } catch (SeckillCloseException | RepeatKillException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("秒杀未开启");
        }
    }
}