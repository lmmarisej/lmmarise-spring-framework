//package org.lmmarise.spring.demo.dao;
//
//import org.lmmarise.spring.demo.entity.Member;
//import org.lmmarise.springfamework.orm.BaseDaoSupport;
//import org.lmmarise.springfamework.orm.common.QueryRule;
//import org.lmmarise.springframework.beans.factory.annotation.LmmAutowired;
//import org.lmmarise.springframework.context.stereotype.LmmRepository;
//
//import javax.sql.DataSource;
//import java.util.List;
//
///**
// * 使用 ORM 框架测试
// *
// * @author lmmarise.j@gmail.com
// * @since 2021/8/26 9:07 下午
// */
//@LmmRepository
//public class MemberDao extends BaseDaoSupport<Member, Long> {
//    @Override
//    protected String getPkColumn() {
//        return "id";
//    }
//
//    @LmmAutowired(value = "dataSource")
//    @Override
//    protected void setDataSource(DataSource dataSource) {
//        super.setDataSourceReadOnly(dataSource);
//        super.setDataSourceWrite(dataSource);
//    }
//
//    public List<Member> selectAll() throws Exception {
//        QueryRule queryRule = QueryRule.getInstance();
//        queryRule.andLike("name", "%Tom%");
//        return super.select(queryRule);
//    }
//}
