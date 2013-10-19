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
 * (created at 2011-5-30)
 */
package org.opencloudb.paser.visitor;

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

/**
 * @author mycat
 */
public interface SQLASTVisitor {

    void visit(BetweenAndExpression node);

    void visit(ComparisionIsExpression node);

    void visit(InExpressionList node);

    void visit(LikeExpression node);

    void visit(CollateExpression node);

    void visit(UserExpression node);

    void visit(UnaryOperatorExpression node);

    void visit(BinaryOperatorExpression node);

    void visit(PolyadicOperatorExpression node);

    void visit(LogicalAndExpression node);

    void visit(LogicalOrExpression node);

    void visit(ComparisionEqualsExpression node);

    void visit(ComparisionNullSafeEqualsExpression node);

    void visit(InExpression node);

    //-------------------------------------------------------
    void visit(FunctionExpression node);

    void visit(Char node);

    void visit(Convert node);

    void visit(Trim node);

    void visit(Cast node);

    void visit(Avg node);

    void visit(Max node);

    void visit(Min node);

    void visit(Sum node);

    void visit(Count node);

    void visit(GroupConcat node);

    void visit(Extract node);

    void visit(Timestampdiff node);

    void visit(Timestampadd node);

    void visit(GetFormat node);

    //-------------------------------------------------------
    void visit(IntervalPrimary node);

    void visit(LiteralBitField node);

    void visit(LiteralBoolean node);

    void visit(LiteralHexadecimal node);

    void visit(LiteralNull node);

    void visit(LiteralNumber node);

    void visit(LiteralString node);

    void visit(CaseWhenOperatorExpression node);

    void visit(DefaultValue node);

    void visit(ExistsPrimary node);

    void visit(PlaceHolder node);

    void visit(Identifier node);

    void visit(MatchExpression node);

    void visit(ParamMarker node);

    void visit(RowExpression node);

    void visit(SysVarPrimary node);

    void visit(UsrDefVarPrimary node);

    //-------------------------------------------------------
    void visit(IndexHint node);

    void visit(InnerJoin node);

    void visit(NaturalJoin node);

    void visit(OuterJoin node);

    void visit(StraightJoin node);

    void visit(SubqueryFactor node);

    void visit(TableReferences node);

    void visit(TableRefFactor node);

    void visit(Dual dual);

    void visit(GroupBy node);

    void visit(Limit node);

    void visit(OrderBy node);

    void visit(ColumnDefinition node);

    void visit(IndexOption node);

    void visit(IndexColumnName node);

    void visit(TableOptions node);

    void visit(AlterSpecification node);

    void visit(DataType node);

    //-------------------------------------------------------
    void visit(ShowAuthors node);

    void visit(ShowBinaryLog node);

    void visit(ShowBinLogEvent node);

    void visit(ShowCharaterSet node);

    void visit(ShowCollation node);

    void visit(ShowColumns node);

    void visit(ShowContributors node);

    void visit(ShowCreate node);

    void visit(ShowDatabases node);

    void visit(ShowEngine node);

    void visit(ShowEngines node);

    void visit(ShowErrors node);

    void visit(ShowEvents node);

    void visit(ShowFunctionCode node);

    void visit(ShowFunctionStatus node);

    void visit(ShowGrants node);

    void visit(ShowIndex node);

    void visit(ShowMasterStatus node);

    void visit(ShowOpenTables node);

    void visit(ShowPlugins node);

    void visit(ShowPrivileges node);

    void visit(ShowProcedureCode node);

    void visit(ShowProcedureStatus node);

    void visit(ShowProcesslist node);

    void visit(ShowProfile node);

    void visit(ShowProfiles node);

    void visit(ShowSlaveHosts node);

    void visit(ShowSlaveStatus node);

    void visit(ShowStatus node);

    void visit(ShowTables node);

    void visit(ShowTableStatus node);

    void visit(ShowTriggers node);

    void visit(ShowVariables node);

    void visit(ShowWarnings node);

    void visit(DescTableStatement node);

    void visit(DALSetStatement node);

    void visit(DALSetNamesStatement node);

    void visit(DALSetCharacterSetStatement node);

    //-------------------------------------------------------
    void visit(DMLCallStatement node);

    void visit(DMLDeleteStatement node);

    void visit(DMLInsertStatement node);

    void visit(DMLReplaceStatement node);

    void visit(DMLSelectStatement node);

    void visit(DMLSelectUnionStatement node);

    void visit(DMLUpdateStatement node);

    void visit(MTSSetTransactionStatement node);

    void visit(MTSSavepointStatement node);

    void visit(MTSReleaseStatement node);

    void visit(MTSRollbackStatement node);

    void visit(DDLTruncateStatement node);

    void visit(DDLAlterTableStatement node);

    void visit(DDLCreateIndexStatement node);

    void visit(DDLCreateTableStatement node);

    void visit(DDLRenameTableStatement node);

    void visit(DDLDropIndexStatement node);

    void visit(DDLDropTableStatement node);

    void visit(ExtDDLCreatePolicy node);

    void visit(ExtDDLDropPolicy node);

}