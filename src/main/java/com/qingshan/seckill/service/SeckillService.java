package com.qingshan.seckill.service;

import com.qingshan.seckill.dto.Exposer;
import com.qingshan.seckill.dto.SeckillExecution;
import com.qingshan.seckill.entity.Seckill;
import com.qingshan.seckill.exception.RepeatKillException;
import com.qingshan.seckill.exception.SeckillCloseException;
import com.qingshan.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    /**
     *  查询全部的秒杀记录.
     * @return 数据库中所有的秒杀记录
     */
    List<Seckill> getSeckillList();

    /**
     *   查询单个秒杀记录
     * @param seckillId   秒杀记录的ID
     * @return   根据ID查询出来的记录信息
     */
    Seckill getById(long seckillId);

    /**
     * 在秒杀开启时输出秒杀接口的地址,否则输出系统时间跟秒杀地址
     * @param seckillId  秒杀商品Id
     * @return  根据对应的状态返回对应的状态实体
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作,有可能是失败的,失败我们就抛出异常
     * @param seckillId  秒杀的商品ID
     * @param userPhone 手机号码
     * @param md5   md5加密值
     * @return   根据不同的结果返回不同的实体信息
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)throws SeckillException, RepeatKillException, SeckillCloseException;
}
