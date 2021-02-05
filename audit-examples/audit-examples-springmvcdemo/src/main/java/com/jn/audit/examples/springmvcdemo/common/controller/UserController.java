/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.audit.examples.springmvcdemo.common.controller;

import com.jn.audit.core.annotation.Resource;
import com.jn.audit.core.annotation.ResourceId;
import com.jn.audit.examples.springmvcdemo.common.dao.UserDao;
import com.jn.audit.examples.springmvcdemo.common.model.User;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.apachedbutils.QueryRunner;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.springjdbc.JdbcTemplate;
import com.jn.sqlhelper.springjdbc.NamedParameterJdbcTemplate;
import com.jn.sqlhelper.springjdbc.resultset.SqlHelperRowMapperResultSetExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostMapping
    public void add(User user) {
        userDao.insert(user);
    }

    @PutMapping("/{id}")
    public void update(@ResourceId String id, @Resource @RequestBody User user) {
        user.setId(id);
        User u = userDao.selectById(id);
        if (u == null) {
            add(user);
        } else {
            userDao.updateById(user);
        }
    }

    @PutMapping()
    public void update(@Resource @RequestBody User user) {
        user.setId(user.getId());
        User u = userDao.selectById(user.getId());
        if (u == null) {
            add(user);
        } else {
            userDao.updateById(user);
        }
    }


    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) {
        userDao.deleteById(id);
    }


    @DeleteMapping("/ids")
    public void deleteByIds(@ResourceId @RequestBody List<String> ids) {
        for (String id : ids) {
            userDao.deleteById(id);
        }
    }


    @DeleteMapping()
    public void deleteByBeans(@Resource @RequestBody List<User> users) {
        for (User user : users) {
            userDao.deleteById(user.getId());
        }
    }

    @GetMapping("/_useMyBatis")
    public PagingResult list_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(value = "namelike", required = false) String namelike,
            @RequestParam(value = "grateAge", required = false, defaultValue = "10") int age) {
        User queryCondition = new User();
        queryCondition.setAge(age);
        queryCondition.setName(namelike);

        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setEscapeLikeParameter(true);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        List<User> users = userDao.selectByLimit(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/subqueryPagination_useMyBatis")
    public PagingResult subqueryPagination_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut) {
        User queryCondition = new User();
        queryCondition.setAge(10);
        queryCondition.setName("zhangsan_");


        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.subqueryPaging(true);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        List<User> users = userDao.selectByLimit_subqueryPagination(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbc_rowMapper")
    public PagingResult list_useSpringJdbc_rowMapper(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "count", required = false) boolean count,
            @RequestParam(name = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > 10[PAGING_END]) n where name like 'zhangsan%' ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > 10");
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User u = new User();
                u.setId(rs.getString("ID"));
                u.setName(rs.getString("NAME"));
                u.setAge(rs.getInt("AGE"));
                return u;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbc_pSetter_rExecutor")
    public PagingResult list__useSpringJdbc_pSetter_rExecutor(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        StringBuilder sqlBuilder = new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, 10);
            }
        }, new ResultSetExtractor<List<User>>() {
            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getString("ID"));
                    u.setName(rs.getString("NAME"));
                    u.setAge(rs.getInt("AGE"));
                    users.add(u);
                }
                return users;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbc_args_rExecutor")
    public PagingResult list__useSpringJdbc_args_rExecutor(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);

        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > ?[PAGING_END]) n where name like CONCAT(?,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");

        Object[] args = testSubquery ? new Object[]{10, "zhangsan"} : new Object[]{10};
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), args, new ResultSetExtractor<List<User>>() {
            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getString("ID"));
                    u.setName(rs.getString("NAME"));
                    u.setAge(rs.getInt("AGE"));
                    users.add(u);
                }
                return users;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/custom_BeanRowMapperTests")
    public PagingResult custom_BeanRowMapperTests(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        StringBuilder sqlBuilder = new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");

        BeanRowMapper<User> beanRowMapper = new BeanRowMapper(User.class);
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, 10);
            }
        }, new SqlHelperRowMapperResultSetExtractor<User>(beanRowMapper));
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        System.out.println(JSONBuilderProvider.simplest().toJson(users));
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbcNamedTemplate")
    public PagingResult list_useSpringJdbcNamedTemplate(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > :age [PAGING_END]) n where name like CONCAT(:name,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > :age");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("age", 10);
        paramMap.put("name", "zhangsan");

        List<User> users = namedJdbcTemplate.query(sqlBuilder.toString(), paramMap, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User u = new User();
                u.setId(rs.getString("ID"));
                u.setName(rs.getString("NAME"));
                u.setAge(rs.getInt("AGE"));
                return u;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/list_useApacheDBUtils")
    public PagingResult list_useApacheDBUtils(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) throws SQLException {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > ?[PAGING_END]) n where name like CONCAT(?,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");

        DataSource ds = namedJdbcTemplate.getJdbcTemplate().getDataSource();
        QueryRunner queryRunner = new QueryRunner(ds);

        List<Object> params = Collects.emptyArrayList();
        params.add(10);
        if (testSubquery) {
            params.add("zhangsan");
        }

        List<User> users = queryRunner.query(sqlBuilder.toString(), new ResultSetHandler<List<User>>() {
            RowMapperResultSetExtractor extractor = new RowMapperResultSetExtractor<User>(new BeanRowMapper<User>(User.class));

            @Override
            public List<User> handle(ResultSet rs) throws SQLException {
                return extractor.extract(rs);
            }
        }, Collects.toArray(params));
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }


    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
    }

    @ApiOperation(value = "upload", consumes = "multipart/form-data")
    @PostMapping("/_upload/{id}")
    public void upload(@RequestPart("files") MultipartFile[] files, @PathVariable("id") String id, @RequestParam("name") String name) {
        logger.info("id: {}", id);
        logger.info("name: {}", name);
        Collects.forEach(files, new Consumer<MultipartFile>() {
            @Override
            public void accept(MultipartFile multipartFile) {
                logger.info("file: {}", multipartFile);
            }
        });

    }

}
