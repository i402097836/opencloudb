/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2011-11-9)
 */
package org.opencloudb.paser.visitor;

import java.util.Collection;

import org.opencloudb.paser.ast.ASTNode;
import org.opencloudb.paser.ast.ddl.ColumnDefinition;
import org.opencloudb.paser.ast.ddl.TableOptions;
import org.opencloudb.paser.ast.ddl.datatype.DataType;
import org.opencloudb.paser.ast.ddl.index.IndexColumnName;
import org.opencloudb.paser.ast.ddl.index.IndexOption;
import org.opencloudb.paser.ast.expression.BinaryOperatorExpression;
import org.opencloudb.paser.ast.expression.PolyadicOperatorExpression;
import org.opencloudb.paser.ast.expression.UnaryOperatorExpression;
import org.opencloudb.paser.ast.expression.comparison.BetweenAndExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionEqualsExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionIsExpression;
import org.opencloudb.paser.ast.expression.comparison.ComparisionNullSafeEqualsExpression;
import org.opencloudb.paser.ast.expression.comparison.InExpression;
import org.opencloudb.paser.ast.expression.function.FunctionExpression;
import org.opencloudb.paser.ast.expression.function.cast.Cast;
import org.opencloudb.paser.ast.expression.function.cast.Convert;
import org.opencloudb.paser.ast.expression.function.datetime.Extract;
import org.opencloudb.paser.ast.expression.function.datetime.GetFormat;
import org.opencloudb.paser.ast.expression.function.datetime.Timestampadd;
import org.opencloudb.paser.ast.expression.function.datetime.Timestampdiff;
import org.opencloudb.paser.ast.expression.function.groupby.Avg;
import org.opencloudb.paser.ast.expression.function.groupby.Count;
import org.opencloudb.paser.ast.expression.function.groupby.GroupConcat;
import org.opencloudb.paser.ast.expression.function.groupby.Max;
import org.opencloudb.paser.ast.expression.function.groupby.Min;
import org.opencloudb.paser.ast.expression.function.groupby.Sum;
import org.opencloudb.paser.ast.expression.function.literal.IntervalPrimary;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBitField;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBoolean;
import org.opencloudb.paser.ast.expression.function.literal.LiteralHexadecimal;
import org.opencloudb.paser.ast.expression.function.literal.LiteralNull;
import org.opencloudb.paser.ast.expression.function.literal.LiteralNumber;
import org.opencloudb.paser.ast.expression.function.literal.LiteralString;
import org.opencloudb.paser.ast.expression.function.string.Char;
import org.opencloudb.paser.ast.expression.function.string.LikeExpression;
import org.opencloudb.paser.ast.expression.function.string.Trim;
import org.opencloudb.paser.ast.expression.function.type.CollateExpression;
import org.opencloudb.paser.ast.expression.logical.LogicalAndExpression;
import org.opencloudb.paser.ast.expression.logical.LogicalOrExpression;
import org.opencloudb.paser.ast.expression.misc.InExpressionList;
import org.opencloudb.paser.ast.expression.misc.UserExpression;
import org.opencloudb.paser.ast.expression.primary.CaseWhenOperatorExpression;
import org.opencloudb.paser.ast.expression.primary.DefaultValue;
import org.opencloudb.paser.ast.expression.primary.ExistsPrimary;
import org.opencloudb.paser.ast.expression.primary.Identifier;
import org.opencloudb.paser.ast.expression.primary.MatchExpression;
import org.opencloudb.paser.ast.expression.primary.ParamMarker;
import org.opencloudb.paser.ast.expression.primary.PlaceHolder;
import org.opencloudb.paser.ast.expression.primary.RowExpression;
import org.opencloudb.paser.ast.expression.primary.SysVarPrimary;
import org.opencloudb.paser.ast.expression.primary.UsrDefVarPrimary;
import org.opencloudb.paser.ast.fragment.GroupBy;
import org.opencloudb.paser.ast.fragment.Limit;
import org.opencloudb.paser.ast.fragment.OrderBy;
import org.opencloudb.paser.ast.stmt.dal.DALSetCharacterSetStatement;
import org.opencloudb.paser.ast.stmt.dal.DALSetNamesStatement;
import org.opencloudb.paser.ast.stmt.dal.DALSetStatement;
import org.opencloudb.paser.ast.stmt.dal.ShowAuthors;
import org.opencloudb.paser.ast.stmt.dal.ShowBinLogEvent;
import org.opencloudb.paser.ast.stmt.dal.ShowBinaryLog;
import org.opencloudb.paser.ast.stmt.dal.ShowCharaterSet;
import org.opencloudb.paser.ast.stmt.dal.ShowCollation;
import org.opencloudb.paser.ast.stmt.dal.ShowColumns;
import org.opencloudb.paser.ast.stmt.dal.ShowContributors;
import org.opencloudb.paser.ast.stmt.dal.ShowCreate;
import org.opencloudb.paser.ast.stmt.dal.ShowDatabases;
import org.opencloudb.paser.ast.stmt.dal.ShowEngine;
import org.opencloudb.paser.ast.stmt.dal.ShowEngines;
import org.opencloudb.paser.ast.stmt.dal.ShowErrors;
import org.opencloudb.paser.ast.stmt.dal.ShowEvents;
import org.opencloudb.paser.ast.stmt.dal.ShowFunctionCode;
import org.opencloudb.paser.ast.stmt.dal.ShowFunctionStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowGrants;
import org.opencloudb.paser.ast.stmt.dal.ShowIndex;
import org.opencloudb.paser.ast.stmt.dal.ShowMasterStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowOpenTables;
import org.opencloudb.paser.ast.stmt.dal.ShowPlugins;
import org.opencloudb.paser.ast.stmt.dal.ShowPrivileges;
import org.opencloudb.paser.ast.stmt.dal.ShowProcedureCode;
import org.opencloudb.paser.ast.stmt.dal.ShowProcedureStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowProcesslist;
import org.opencloudb.paser.ast.stmt.dal.ShowProfile;
import org.opencloudb.paser.ast.stmt.dal.ShowProfiles;
import org.opencloudb.paser.ast.stmt.dal.ShowSlaveHosts;
import org.opencloudb.paser.ast.stmt.dal.ShowSlaveStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowTableStatus;
import org.opencloudb.paser.ast.stmt.dal.ShowTables;
import org.opencloudb.paser.ast.stmt.dal.ShowTriggers;
import org.opencloudb.paser.ast.stmt.dal.ShowVariables;
import org.opencloudb.paser.ast.stmt.dal.ShowWarnings;
import org.opencloudb.paser.ast.stmt.ddl.DDLAlterTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLCreateIndexStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLCreateTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLDropIndexStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLDropTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLRenameTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLTruncateStatement;
import org.opencloudb.paser.ast.stmt.ddl.DescTableStatement;
import org.opencloudb.paser.ast.stmt.ddl.DDLAlterTableStatement.AlterSpecification;
import org.opencloudb.paser.ast.stmt.dml.DMLCallStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLDeleteStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLInsertStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLReplaceStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLSelectStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLSelectUnionStatement;
import org.opencloudb.paser.ast.stmt.dml.DMLUpdateStatement;
import org.opencloudb.paser.ast.stmt.extension.ExtDDLCreatePolicy;
import org.opencloudb.paser.ast.stmt.extension.ExtDDLDropPolicy;
import org.opencloudb.paser.ast.stmt.mts.MTSReleaseStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSRollbackStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSSavepointStatement;
import org.opencloudb.paser.ast.stmt.mts.MTSSetTransactionStatement;
import org.opencloudb.paser.ast.tableref.Dual;
import org.opencloudb.paser.ast.tableref.IndexHint;
import org.opencloudb.paser.ast.tableref.InnerJoin;
import org.opencloudb.paser.ast.tableref.NaturalJoin;
import org.opencloudb.paser.ast.tableref.OuterJoin;
import org.opencloudb.paser.ast.tableref.StraightJoin;
import org.opencloudb.paser.ast.tableref.SubqueryFactor;
import org.opencloudb.paser.ast.tableref.TableRefFactor;
import org.opencloudb.paser.ast.tableref.TableReferences;
import org.opencloudb.paser.util.Pair;

/**
 * @author mycat
 */
public class EmptySQLASTVisitor implements SQLASTVisitor {

    @SuppressWarnings({ "rawtypes" })
    private void visitInternal(Object obj) {
        if (obj == null) return;
        if (obj instanceof ASTNode) {
            ((ASTNode) obj).accept(this);
        } else if (obj instanceof Collection) {
            for (Object o : (Collection) obj) {
                visitInternal(o);
            }
        } else if (obj instanceof Pair) {
            visitInternal(((Pair) obj).getKey());
            visitInternal(((Pair) obj).getValue());
        }
    }

    @Override
    public void visit(BetweenAndExpression node) {
        visitInternal(node.getFirst());
        visitInternal(node.getSecond());
        visitInternal(node.getThird());
    }

    @Override
    public void visit(ComparisionIsExpression node) {
        visitInternal(node.getOperand());
    }

    @Override
    public void visit(InExpressionList node) {
        visitInternal(node.getList());
    }

    @Override
    public void visit(LikeExpression node) {
        visitInternal(node.getFirst());
        visitInternal(node.getSecond());
        visitInternal(node.getThird());
    }

    @Override
    public void visit(CollateExpression node) {
        visitInternal(node.getString());
    }

    @Override
    public void visit(UserExpression node) {
    }

    @Override
    public void visit(UnaryOperatorExpression node) {
        visitInternal(node.getOperand());
    }

    @Override
    public void visit(BinaryOperatorExpression node) {
        visitInternal(node.getLeftOprand());
        visitInternal(node.getRightOprand());
    }

    @Override
    public void visit(PolyadicOperatorExpression node) {
        for (int i = 0, len = node.getArity(); i < len; ++i) {
            visitInternal(node.getOperand(i));
        }
    }

    @Override
    public void visit(LogicalAndExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    @Override
    public void visit(LogicalOrExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    @Override
    public void visit(ComparisionEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(ComparisionNullSafeEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(InExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(FunctionExpression node) {
        visitInternal(node.getArguments());
    }

    @Override
    public void visit(Char node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Convert node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Trim node) {
        visit((FunctionExpression) node);
        visitInternal(node.getRemainString());
        visitInternal(node.getString());
    }

    @Override
    public void visit(Cast node) {
        visit((FunctionExpression) node);
        visitInternal(node.getExpr());
        visitInternal(node.getTypeInfo1());
        visitInternal(node.getTypeInfo2());

    }

    @Override
    public void visit(Avg node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Max node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Min node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Sum node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Count node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(GroupConcat node) {
        visit((FunctionExpression) node);
        visitInternal(node.getAppendedColumnNames());
        visitInternal(node.getOrderBy());
    }

    @Override
    public void visit(Timestampdiff node) {
    }

    @Override
    public void visit(Timestampadd node) {
    }

    @Override
    public void visit(Extract node) {
    }

    @Override
    public void visit(GetFormat node) {
    }

    @Override
    public void visit(IntervalPrimary node) {
        visitInternal(node.getQuantity());
    }

    @Override
    public void visit(LiteralBitField node) {
    }

    @Override
    public void visit(LiteralBoolean node) {
    }

    @Override
    public void visit(LiteralHexadecimal node) {
    }

    @Override
    public void visit(LiteralNull node) {
    }

    @Override
    public void visit(LiteralNumber node) {
    }

    @Override
    public void visit(LiteralString node) {
    }

    @Override
    public void visit(CaseWhenOperatorExpression node) {
        visitInternal(node.getComparee());
        visitInternal(node.getElseResult());
        visitInternal(node.getWhenList());
    }

    @Override
    public void visit(DefaultValue node) {
    }

    @Override
    public void visit(ExistsPrimary node) {
        visitInternal(node.getSubquery());
    }

    @Override
    public void visit(PlaceHolder node) {
    }

    @Override
    public void visit(Identifier node) {
    }

    @Override
    public void visit(MatchExpression node) {
        visitInternal(node.getColumns());
        visitInternal(node.getPattern());
    }

    @Override
    public void visit(ParamMarker node) {
    }

    @Override
    public void visit(RowExpression node) {
        visitInternal(node.getRowExprList());
    }

    @Override
    public void visit(SysVarPrimary node) {
    }

    @Override
    public void visit(UsrDefVarPrimary node) {
    }

    @Override
    public void visit(IndexHint node) {
    }

    @Override
    public void visit(InnerJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(NaturalJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(OuterJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(StraightJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(SubqueryFactor node) {
        visitInternal(node.getSubquery());
    }

    @Override
    public void visit(TableReferences node) {
        visitInternal(node.getTableReferenceList());
    }

    @Override
    public void visit(TableRefFactor node) {
        visitInternal(node.getHintList());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(Dual dual) {
    }

    @Override
    public void visit(GroupBy node) {
        visitInternal(node.getOrderByList());
    }

    @Override
    public void visit(Limit node) {
        visitInternal(node.getOffset());
        visitInternal(node.getSize());
    }

    @Override
    public void visit(OrderBy node) {
        visitInternal(node.getOrderByList());
    }

    @Override
    public void visit(ColumnDefinition columnDefinition) {
    }

    @Override
    public void visit(IndexOption indexOption) {
    }

    @Override
    public void visit(IndexColumnName indexColumnName) {
    }

    @Override
    public void visit(TableOptions node) {
    }

    @Override
    public void visit(AlterSpecification node) {
    }

    @Override
    public void visit(DataType node) {
    }

    @Override
    public void visit(ShowAuthors node) {
    }

    @Override
    public void visit(ShowBinaryLog node) {
    }

    @Override
    public void visit(ShowBinLogEvent node) {
        visitInternal(node.getLimit());
        visitInternal(node.getPos());
    }

    @Override
    public void visit(ShowCharaterSet node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowCollation node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowColumns node) {
        visitInternal(node.getTable());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowContributors node) {
    }

    @Override
    public void visit(ShowCreate node) {
        visitInternal(node.getId());
    }

    @Override
    public void visit(ShowDatabases node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowEngine node) {
    }

    @Override
    public void visit(ShowEngines node) {
    }

    @Override
    public void visit(ShowErrors node) {
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(ShowEvents node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowFunctionCode node) {
        visitInternal(node.getFunctionName());
    }

    @Override
    public void visit(ShowFunctionStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowGrants node) {
        visitInternal(node.getUser());
    }

    @Override
    public void visit(ShowIndex node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(ShowMasterStatus node) {
    }

    @Override
    public void visit(ShowOpenTables node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowPlugins node) {
    }

    @Override
    public void visit(ShowPrivileges node) {
    }

    @Override
    public void visit(ShowProcedureCode node) {
        visitInternal(node.getProcedureName());
    }

    @Override
    public void visit(ShowProcedureStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowProcesslist node) {
    }

    @Override
    public void visit(ShowProfile node) {
        visitInternal(node.getForQuery());
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(ShowProfiles node) {
    }

    @Override
    public void visit(ShowSlaveHosts node) {
    }

    @Override
    public void visit(ShowSlaveStatus node) {
    }

    @Override
    public void visit(ShowStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTables node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTableStatus node) {
        visitInternal(node.getDatabase());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTriggers node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowVariables node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowWarnings node) {
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(DescTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DALSetStatement node) {
        visitInternal(node.getAssignmentList());
    }

    @Override
    public void visit(DALSetNamesStatement node) {
    }

    @Override
    public void visit(DALSetCharacterSetStatement node) {
    }

    @Override
    public void visit(DMLCallStatement node) {
        visitInternal(node.getArguments());
        visitInternal(node.getProcedure());
    }

    @Override
    public void visit(DMLDeleteStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getTableNames());
        visitInternal(node.getTableRefs());
        visitInternal(node.getWhereCondition());
    }

    @Override
    public void visit(DMLInsertStatement node) {
        visitInternal(node.getColumnNameList());
        visitInternal(node.getDuplicateUpdate());
        visitInternal(node.getRowList());
        visitInternal(node.getSelect());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DMLReplaceStatement node) {
        visitInternal(node.getColumnNameList());
        visitInternal(node.getRowList());
        visitInternal(node.getSelect());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DMLSelectStatement node) {
        visitInternal(node.getGroup());
        visitInternal(node.getHaving());
        visitInternal(node.getLimit());
        visitInternal(node.getOrder());
        visitInternal(node.getSelectExprList());
        visitInternal(node.getTables());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(DMLSelectUnionStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getSelectStmtList());
    }

    @Override
    public void visit(DMLUpdateStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getTableRefs());
        visitInternal(node.getValues());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(MTSSetTransactionStatement node) {
    }

    @Override
    public void visit(MTSSavepointStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(MTSReleaseStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(MTSRollbackStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(DDLTruncateStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLAlterTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLCreateIndexStatement node) {
        visitInternal(node.getIndexName());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLCreateTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLRenameTableStatement node) {
        visitInternal(node.getList());
    }

    @Override
    public void visit(DDLDropIndexStatement node) {
        visitInternal(node.getIndexName());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLDropTableStatement node) {
        visitInternal(node.getTableNames());
    }

    @Override
    public void visit(ExtDDLCreatePolicy node) {
    }

    @Override
    public void visit(ExtDDLDropPolicy node) {
    }

}