package org.lmmarise.spring.demo.entity;

import lombok.Data;
import org.lmmarise.springfamework.orm.annotation.Entity;
import org.lmmarise.springfamework.orm.annotation.Id;
import org.lmmarise.springfamework.orm.annotation.Table;

import java.io.Serializable;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 9:00 下午
 */
@Entity
@Table(name = "t_member")
@Data
public class Member implements Serializable {
    @Id
    private Long id;
    private String name;
    private String addr;
    private Integer age;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", age=" + age +
                '}';
    }
}
