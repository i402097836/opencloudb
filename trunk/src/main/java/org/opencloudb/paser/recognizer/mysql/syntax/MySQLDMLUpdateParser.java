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
 * (created at 2011-5-19)
 */
package org.opencloudb.paser.recognizer.mysql.syntax;

import static org.opencloudb.paser.recognizer.mysql.MySQLToken.KW_IGNORE;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.KW_LOW_PRIORITY;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.KW_SET;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.KW_UPDATE;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.KW_WHERE;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.OP_ASSIGN;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.OP_EQUALS;
import static org.opencloudb.paser.recognizer.mysql.MySQLToken.PUNC_COMMA;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.primary.Identifier;
import org.opencloudb.paser.ast.fragment.Limit;
import org.opencloudb.paser.ast.fragment.OrderBy;
import org.opencloudb.paser.ast.stmt.dml.DMLUpdateStatement;
import org.opencloudb.paser.ast.tableref.TableReferences;
import org.opencloudb.paser.recognizer.mysql.lexer.MySQLLexer;
import org.opencloudb.paser.util.Pair;

/**
 * @author mycat
 */
public class MySQLDMLUpdateParser extends MySQLDMLParser {
    public MySQLDMLUpdateParser(MySQLLexer lexer, MySQLExprParser exprParser) {
        super(lexer, exprParser);
    }

    /**
     * nothing has been pre-consumed <code><pre>
     * 'UPDATE' 'LOW_PRIORITY'? 'IGNORE'? table_reference
     *   'SET' colName ('='|':=') (expr|'DEFAULT') (',' colName ('='|':=') (expr|'DEFAULT'))*
     *     ('WHERE' cond)?
     *     {singleTable}? => ('ORDER' 'BY' orderBy)?  ('LIMIT' count)?
     * </pre></code>
     */
    public DMLUpdateStatement update() throws SQLSyntaxErrorException {
        match(KW_UPDATE);
        boolean lowPriority = false;
        boolean ignore = false;
        if (lexer.token() == KW_LOW_PRIORITY) {
            lexer.nextToken();
            lowPriority = true;
        }
        if (lexer.token() == KW_IGNORE) {
            lexer.nextToken();
            ignore = true;
        }
        TableReferences tableRefs = tableRefs();
        match(KW_SET);
        List<Pair<Identifier, Expression>> values;
        Identifier col = identifier();
        match(OP_EQUALS, OP_ASSIGN);
        Expression expr = exprParser.expression();
        if (lexer.token() == PUNC_COMMA) {
            values = new LinkedList<Pair<Identifier, Expression>>();
            values.add(new Pair<Identifier, Expression>(col, expr));
            for (; lexer.token() == PUNC_COMMA;) {
                lexer.nextToken();
                col = identifier();
                match(OP_EQUALS, OP_ASSIGN);
                expr = exprParser.expression();
                values.add(new Pair<Identifier, Expression>(col, expr));
            }
        } else {
            values = new ArrayList<Pair<Identifier, Expression>>(1);
            values.add(new Pair<Identifier, Expression>(col, expr));
        }
        Expression where = null;
        if (lexer.token() == KW_WHERE) {
            lexer.nextToken();
            where = exprParser.expression();
        }
        OrderBy orderBy = null;
        Limit limit = null;
        if (tableRefs.isSingleTable()) {
            orderBy = orderBy();
            limit = limit();
        }
        return new DMLUpdateStatement(lowPriority, ignore, tableRefs, values, where, orderBy, limit);
    }
}