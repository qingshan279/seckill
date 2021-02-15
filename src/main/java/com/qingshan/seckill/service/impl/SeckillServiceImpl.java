package com.qingshan.seckill.service.impl;

import com.qingshan.seckill.dao.SeckillMapper;
import com.qingshan.seckill.dao.SuccessKilledMapper;
import com.qingshan.seckill.dto.Exposer;
import com.qingshan.seckill.dto.SeckillExecution;
import com.qingshan.seckill.entity.Seckill;
import com.qingshan.seckill.entity.SuccessKilled;
import com.qingshan.seckill.enums.SeckillStatEnum;
import com.qingshan.seckill.exception.RepeatKillException;
import com.qingshan.seckill.exception.SeckillCloseException;
import com.qingshan.seckill.exception.SeckillException;
import com.qingshan.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /* 加入一个盐值,用于混淆*/
    private final String salt = "thisIsASaltValue";

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;

    /**
     * 查询全部的秒杀记录
     *
     * @return 数据库中所有的秒杀记录
     */
    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0, 4);
    }

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId 秒杀记录的ID
     * @return 根据ID查询出来的记录信息
     */
    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    /**
     * 秒杀开始时输出秒杀接口的地址，否则输出系统时间和秒杀地址
     *
     * @param seckillId 秒杀商品Id
     * @return 根据对应的状态返回对应的状态实体
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 根据秒杀的ID去查询是否存在这个商品
        Seckill seckill = seckillMapper.queryById(seckillId);
        if (seckill == null) {
            logger.warn("查询不到这个秒杀产品的记录");
            return new Exposer(false, seckillId);
        }

        // 判断是否还没到秒杀时间或者是过了秒杀时间
        Timestamp startTime = seckill.getStartTime();
        Timestamp endTime = seckill.getEndTime();
        Timestamp nowTime = Timestamp.from(Instant.now());

        //   开始时间大于现在的时候说明没有开始秒杀活动    秒杀活动结束时间小于现在的时间说明秒杀已经结束了
        if (nowTime.after(startTime) && nowTime.before(endTime)) {
            //秒杀开启,返回秒杀商品的id,用给接口加密的md5
            String md5 = getMd5(seckillId);
            return new Exposer(true, md5, seckillId);
        }
        return new Exposer(false, seckillId, nowTime, startTime, endTime);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            logger.error("秒杀数据被篡改");
            throw new SeckillException("seckill data rewrite");
        }
        // 执行秒杀业务逻辑
        Timestamp nowTime = Timestamp.from(Instant.now());

        try {
            //执行减库存操作
            int reduceNumber = seckillMapper.reduceNumber(seckillId, nowTime);
            if (reduceNumber <= 0) {
                logger.warn("没有更新数据库记录,说明秒杀结束");
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 这里至少减少的数量不为0了,秒杀成功了就增加一个秒杀成功详细
                int insertCount = successKilledMapper.insertSuccessKilled(seckillId, userPhone);
                // 查看是否被重复插入,即用户是否重复秒杀
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功了,返回那条插入成功秒杀的信息
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e ) {
            throw e;
        } catch (RepeatKillException e ) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 把编译期异常转换为运行时异常
            throw new SeckillException("seckill inner error : " + e.getMessage());
        }
    }
}
