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
 * (created at 2011-11-11)
 */
package org.opencloudb.parser.ast.expression;

import junit.framework.TestCase;

import org.junit.Assert;
import org.opencloudb.paser.ast.expression.function.literal.LiteralHexadecimal;
import org.opencloudb.paser.recognizer.mysql.lexer.MySQLLexer;
import org.opencloudb.paser.recognizer.mysql.syntax.MySQLExprParser;

/**
 * @author mycat
 */
public class LiteralHexadecimalTest extends TestCase {
    public void testUtf8() throws Exception {
        String sql = "x'E982B1E7A195275C73'";
        LiteralHexadecimal hex = (LiteralHexadecimal) new MySQLExprParser(new MySQLLexer(sql), "utf-8").expression();
        Assert.assertEquals("邱硕'\\s", hex.evaluation(null));

        sql = "x'd0A'";
        hex = (LiteralHexadecimal) new MySQLExprParser(new MySQLLexer(sql), "utf-8").expression();
        Assert.assertEquals("\r\n", hex.evaluation(null));
    }

}