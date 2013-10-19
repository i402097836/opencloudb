package org.opencloudb.route.function;
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
 * (created at 2011-11-21)
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencloudb.config.model.rule.RuleAlgorithm;
import org.opencloudb.paser.ast.expression.Expression;
import org.opencloudb.paser.ast.expression.function.FunctionExpression;


/**
 * @author mycat
 */
public class PartitionByIntegerFileMap extends FunctionExpression implements RuleAlgorithm {
	
	  public PartitionByIntegerFileMap(String functionName)
	  {
		  this(functionName,null);
	  }
    public PartitionByIntegerFileMap(String functionName, List<Expression> arguments) {
        super(functionName, arguments);
    }

    private Integer defaultNode;
    private String fileMapPath;

    public void setDefaultNode(Integer defaultNode) {
        this.defaultNode = defaultNode;
    }

    public void setFileMapPath(String fileMapPath) {
        this.fileMapPath = fileMapPath;
    }

    private Map<Integer, Integer> app2Partition;

    @Override
    public void init() {
        InputStream fin = null;
        try {
        	if(fileMapPath.contains(File.separator))
        	{
            fin = new FileInputStream(new File(fileMapPath));
        	}else
        	{//classpath load
        		fin = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileMapPath);
        	}
            BufferedReader in = new BufferedReader(new InputStreamReader(fin));
            app2Partition = new HashMap<Integer, Integer>();
            for (String line = null; (line = in.readLine()) != null;) {
                line = line.trim();
                if (line.startsWith("#") || line.startsWith("//")) continue;
                int ind = line.indexOf('=');
                if (ind < 0) continue;
                try {
                    int key = Integer.parseInt(line.substring(0, ind).trim());
                    int pid = Integer.parseInt(line.substring(ind + 1).trim());
                    app2Partition.put(key, pid);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fin.close();
            } catch (Exception e2) {
            }
        }
    }

    @Override
    public Object evaluationInternal(Map<? extends Object, ? extends Object> parameters) {
        Object arg = arguments.get(0).evaluation(parameters);
        if (arg == null) {
            throw new IllegalArgumentException("partition key is null ");
        } else if (arg == UNEVALUATABLE) {
            throw new IllegalArgumentException("argument is UNEVALUATABLE");
        }
        Integer pid = app2Partition.get(arg);
        //if (pid == null) return defaultNode;
        if (pid == null) return null;
        return pid;
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        if (arguments == null || arguments.size() != 1)
            throw new IllegalArgumentException("function "
                                               + getFunctionName()
                                               + " must have 1 arguments but is "
                                               + arguments);
        PartitionByIntegerFileMap rst = new PartitionByIntegerFileMap(functionName, arguments);
        rst.fileMapPath = fileMapPath;
        rst.defaultNode = defaultNode;
        return rst;
    }

	@Override
	public RuleAlgorithm constructMe(Object... objects) {
		  List<Expression> args = new ArrayList<Expression>(objects.length);
	        for (Object obj : objects) {
	            args.add((Expression) obj);
	        }
	        PartitionByIntegerFileMap obj=new PartitionByIntegerFileMap(this.functionName,args);
	        obj.fileMapPath=this.fileMapPath;
	        obj.defaultNode=defaultNode;
	        return obj;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer[] calculate(
			Map<? extends Object, ? extends Object> parameters) {
		Integer[] rst = new Integer[1];
        Object arg = arguments.get(0).evaluation(parameters);
        if (arg == null) {
            throw new IllegalArgumentException("partition key is null ");
        } else if (arg == UNEVALUATABLE) {
            throw new IllegalArgumentException("argument is UNEVALUATABLE");
        }
        Number key;
        if (arg instanceof Number) {
            key = (Number) arg;
        } else if (arg instanceof String) {
            key = Integer.parseInt((String) arg);
        } else {
            throw new IllegalArgumentException("unsupported data type for partition key: " + arg.getClass());
        }
        rst[0] = app2Partition.get(key.intValue()); 
        return rst;
	}

}