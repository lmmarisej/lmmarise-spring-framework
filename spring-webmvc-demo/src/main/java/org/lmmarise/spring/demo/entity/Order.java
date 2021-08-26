package org.lmmarise.spring.demo.entity;

import lombok.Data;
import org.lmmarise.springfamework.orm.annotation.Column;
import org.lmmarise.springfamework.orm.annotation.Entity;
import org.lmmarise.springfamework.orm.annotation.Id;
import org.lmmarise.springfamework.orm.annotation.Table;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:03 下午
 */
@Entity
@Table(name = "t_order")
@Data
public class Order {
    @Id
    private Long id;
    @Column(name = "mid")
    private Long memberId;
    private String detail;
    private Long createTime;
    private String createTimeFmt;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", detail='" + detail + '\'' +
                ", createTime=" + createTime +
                ", createTimeFmt='" + createTimeFmt + '\'' +
                '}';
    }
}
