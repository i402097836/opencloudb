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
 * (created at 2011-1-12)
 */
package org.opencloudb.paser.ast.expression.logical;

import java.util.Map;

import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.UnaryOperatorExpression;
import org.opencloudb.paser.ast.expression.function.literal.LiteralBoolean;
import org.opencloudb.paser.util.ExprEvalUtils;

/**
 * <code>'NOT' higherExpr</code>
 * 
 * @author mycat
 */
public class LogicalNotExpression extends UnaryOperatorExpression {
    public LogicalNotExpression(Expression operand) {
        super(operand, PRECEDENCE_LOGICAL_NOT);
    }

    @Override
    public String getOperator() {
        return "NOT";
    }

    @Override
    public Object evaluationInternal(Map<? extends Object, ? extends Object> parameters) {
        Object operand = getOperand().evaluation(parameters);
        if (operand == null) return null;
        if (operand == UNEVALUATABLE) return UNEVALUATABLE;
        boolean bool = ExprEvalUtils.obj2bool(operand);
        return bool ? LiteralBoolean.FALSE : LiteralBoolean.TRUE;
    }
}