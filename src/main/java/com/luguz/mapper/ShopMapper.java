package com.luguz.mapper;

import com.luguz.pojo.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */
public interface ShopMapper extends BaseMapper<Shop> {

    @Select(value = "SELECT * FROM shop WHERE id =#{shopId} FOR UPDATE")
    Shop querySecondKillForUpdate(@Param("shopId") Integer shopId);

//当我们执行update语句的时候，实际上会对记录加独占锁（X锁）。另外其他事务对持有独占锁的记录进行修改的时候会被阻塞。这个锁并不是执行完update语句才会释放，而是会等事务结束时才会释放。
//InnoDB事务中，对记录加锁的基本单位是next-key锁。但是会因为一些条件会降级成间隙锁，或者记录锁。加锁的位置准确的说是加载索引上，而不是行上。
//update 语句的 where 条件没有使用索引，就会全表扫描，于是就会对所有记录加上 next-key 锁（记录锁 + 间隙锁），相当于把整个表锁住了。如果存在索引的话，则对索引进行加锁。
    @Update(value = "UPDATE shop SET number =number -1 WHERE id=#{shopId} AND number > 0")
    int updateSecondKillById(@Param("shopId") long shopId);

//    乐观锁

    @Update(value = "UPDATE shop  SET number=number-1,version=version+1 WHERE id=#{shopId} AND version = #{version}")
    int updateSecondKillByVersion( @Param("shopId") long shopId, @Param("version")int version);

}
