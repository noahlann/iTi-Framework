/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.common.data.orm.binding.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联注解条件解析器
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@NoArgsConstructor
public class ConditionParser implements ExpressionVisitor, ItemsListVisitor {

    private List<String> errorMsgList = null;
    private List<Expression> expressList = new ArrayList<>();

    /**
     * 添加错误信息
     *
     * @param msg 错误信息
     */
    private void addErrorMsg(String msg) {
        if (errorMsgList == null) {
            errorMsgList = new ArrayList<>();
        }
        if (!errorMsgList.contains(msg)) {
            errorMsgList.add(msg);
        }
    }

    public List<Expression> getExpressList() throws Exception {
        if (CollUtil.isEmpty(expressList)) {
            throw new Exception(ArrayUtil.join(errorMsgList, "; "));
        }
        return expressList;
    }

    // region 支持的条件
    @Override
    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        if (!(equalsTo.getLeftExpression() instanceof Column)) {
            addErrorMsg("=条件 左侧必须为字段/列名");
        }
        expressList.add(equalsTo);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        if (!(notEqualsTo.getLeftExpression() instanceof Column)) {
            addErrorMsg("!=条件 左侧必须为字段/列名");
        }
        expressList.add(notEqualsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        if (!(greaterThan.getLeftExpression() instanceof Column)) {
            addErrorMsg(">条件 左侧必须为字段/列名");
        }
        expressList.add(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        if (!(greaterThanEquals.getLeftExpression() instanceof Column)) {
            addErrorMsg(">=条件 左侧必须为字段/列名");
        }
        expressList.add(greaterThanEquals);
    }

    @Override
    public void visit(MinorThan minorThan) {
        if (!(minorThan.getLeftExpression() instanceof Column)) {
            addErrorMsg("<条件 左侧必须为字段/列名");
        }
        expressList.add(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        if (!(minorThanEquals.getLeftExpression() instanceof Column)) {
            addErrorMsg("<=条件 左侧必须为字段/列名");
        }
        expressList.add(minorThanEquals);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        if (!(isNullExpression.getLeftExpression() instanceof Column)) {
            addErrorMsg("IsNull条件 左侧必须为字段/列名");
        }
        expressList.add(isNullExpression);
    }

    @Override
    public void visit(InExpression inExpression) {
        if (!(inExpression.getLeftExpression() instanceof Column)) {
            addErrorMsg("IN条件 左侧必须为字段/列名");
        }
        expressList.add(inExpression);
    }

    @Override
    public void visit(Between between) {
        if (!(between.getLeftExpression() instanceof Column)) {
            addErrorMsg("Between条件 左侧必须为字段/列名");
        }
        expressList.add(between);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        if (!(likeExpression.getLeftExpression() instanceof Column)) {
            addErrorMsg("Like条件 左侧必须为字段/列名");
        }
        expressList.add(likeExpression);
    }

    // endregion

    // region 暂未支持的条件
    @Override
    public void visit(OrExpression orExpression) {
        addErrorMsg("暂不支持 OR 关联条件");
    }
    // endregion

    // region 忽略的条件
    @Override
    public void visit(BitwiseRightShift bitwiseRightShift) {

    }

    @Override
    public void visit(BitwiseLeftShift bitwiseLeftShift) {

    }

    @Override
    public void visit(NullValue nullValue) {

    }

    @Override
    public void visit(Function function) {

    }

    @Override
    public void visit(SignedExpression signedExpression) {

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {

    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {

    }

    @Override
    public void visit(DoubleValue doubleValue) {

    }

    @Override
    public void visit(LongValue longValue) {

    }

    @Override
    public void visit(HexValue hexValue) {

    }

    @Override
    public void visit(DateValue dateValue) {

    }

    @Override
    public void visit(TimeValue timeValue) {

    }

    @Override
    public void visit(TimestampValue timestampValue) {

    }

    @Override
    public void visit(Parenthesis parenthesis) {

    }

    @Override
    public void visit(StringValue stringValue) {

    }

    @Override
    public void visit(Addition addition) {

    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(IntegerDivision integerDivision) {

    }

    @Override
    public void visit(Multiplication multiplication) {

    }

    @Override
    public void visit(Subtraction subtraction) {

    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {

    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {

    }

    @Override
    public void visit(Column column) {

    }

    @Override
    public void visit(SubSelect subSelect) {

    }

    @Override
    public void visit(ExpressionList expressionList) {

    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {

    }

    @Override
    public void visit(MultiExpressionList multiExpressionList) {

    }

    @Override
    public void visit(CaseExpression caseExpression) {

    }

    @Override
    public void visit(WhenClause whenClause) {

    }

    @Override
    public void visit(ExistsExpression existsExpression) {

    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {

    }

    @Override
    public void visit(Concat concat) {

    }

    @Override
    public void visit(Matches matches) {

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {

    }

    @Override
    public void visit(CastExpression castExpression) {

    }

    @Override
    public void visit(Modulo modulo) {

    }

    @Override
    public void visit(AnalyticExpression analyticExpression) {

    }

    @Override
    public void visit(ExtractExpression extractExpression) {

    }

    @Override
    public void visit(IntervalExpression intervalExpression) {

    }

    @Override
    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

    }

    @Override
    public void visit(RegExpMatchOperator regExpMatchOperator) {

    }

    @Override
    public void visit(JsonExpression jsonExpression) {

    }

    @Override
    public void visit(JsonOperator jsonOperator) {

    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {

    }

    @Override
    public void visit(UserVariable userVariable) {

    }

    @Override
    public void visit(NumericBind numericBind) {

    }

    @Override
    public void visit(KeepExpression keepExpression) {

    }

    @Override
    public void visit(MySQLGroupConcat mySQLGroupConcat) {

    }

    @Override
    public void visit(ValueListExpression valueListExpression) {

    }

    @Override
    public void visit(RowConstructor rowConstructor) {

    }

    @Override
    public void visit(OracleHint oracleHint) {

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {

    }

    @Override
    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {

    }

    @Override
    public void visit(NextValExpression nextValExpression) {

    }

    @Override
    public void visit(CollateExpression collateExpression) {

    }

    @Override
    public void visit(SimilarToExpression similarToExpression) {

    }

    @Override
    public void visit(ArrayExpression arrayExpression) {

    }

    // endregion
}
