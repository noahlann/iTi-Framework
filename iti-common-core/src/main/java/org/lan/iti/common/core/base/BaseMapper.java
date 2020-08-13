/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.base;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 框架通用 DAO
 * 基于mybatis-plus扩展,将其IService方法移植过来,service层应尽量避免与mapper层存在相同意义的方法
 *
 * @author NorthLan
 * @date 2020/02/20
 * @url https://noahlan.com
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    Logger log = LoggerFactory.getLogger(BaseMapper.class);
    int DEFAULT_BATCH_SIZE = 1000;

    @UtilityClass
    class Helper {
        public Class<?> currentModelClass() {
            return ReflectionKit.getSuperClassGenericType(BaseMapper.class, 1);
        }

        /**
         * 获取 SqlStatement
         *
         * @param sqlMethod ignore
         * @return ignore
         */
        public String sqlStatement(SqlMethod sqlMethod) {
            return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
        }

        /**
         * 执行批量操作
         *
         * @param list      数据集合
         * @param batchSize 批量大小
         * @param consumer  执行方法
         * @param <E>       泛型
         * @return 操作结果
         */
        public <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
            Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
            if (CollectionUtils.isEmpty(list)) {
                return false;
            }
            SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(currentModelClass());
            SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
            boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
            if (sqlSessionHolder != null) {
                SqlSession sqlSession = sqlSessionHolder.getSqlSession();
                // 原生无法支持执行器切换，当存在批量操作时，会嵌套两个session的，优先commit上一个session
                // 按道理来说，这里的值应该一直为false。
                sqlSession.commit(!transaction);
            }
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            if (!transaction) {
                log.warn("SqlSession [" + sqlSession + "] was not registered for synchronization because DataSource is not transactional");
            }
            try {
//                consumer.accept(sqlSession);
                int size = list.size();
                int i = 1;
                for (E element : list) {
                    consumer.accept(sqlSession, element);
                    if ((i % batchSize == 0) || i == size) {
                        sqlSession.flushStatements();
                    }
                    i++;
                }
                //非事物情况下，强制commit。
                sqlSession.commit(!transaction);
                return true;
            } catch (Throwable t) {
                sqlSession.rollback();
                Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
                if (unwrapped instanceof RuntimeException) {
                    MyBatisExceptionTranslator myBatisExceptionTranslator
                            = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true);
                    throw Objects.requireNonNull(myBatisExceptionTranslator.translateExceptionIfPossible((RuntimeException) unwrapped));
                }
                throw ExceptionUtils.mpe(unwrapped);
            } finally {
                sqlSession.close();
            }
        }
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertBatch(Collection<T> entityList) {
        return insertBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = Helper.sqlStatement(SqlMethod.INSERT_ONE);
        return Helper.executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return insertOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(Helper.currentModelClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return Helper.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            if (StringUtils.checkValNull(idVal) || Objects.isNull(selectById((Serializable) idVal))) {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            } else {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
            }
        });
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = Helper.sqlStatement(SqlMethod.UPDATE_BY_ID);
        return Helper.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(selectById((Serializable) idVal)) ?
                    SqlHelper.retBool(insert(entity)) : SqlHelper.retBool(updateById(entity));
        }
        return false;
    }
}
