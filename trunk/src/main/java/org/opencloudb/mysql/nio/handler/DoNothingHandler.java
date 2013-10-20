package org.opencloudb.mysql.nio.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.mysql.nio.MySQLConnection;
/**
 * wuzh
 * @author mycat
 *
 */
public class DoNothingHandler implements ResponseHandler{
	  private static final Logger logger = Logger.getLogger(DoNothingHandler.class);
	@Override
	public void connectionAcquired(MySQLConnection conn) {
		logger.info("connected successfuly "+conn);
		
	}

	@Override
	public void connectionError(Throwable e, MySQLConnection conn) {
		logger.warn("connect error "+conn,e);
		
	}

	@Override
	public void errorResponse(byte[] err, MySQLConnection conn) {
		logger.warn("caught error resp: "+conn+" "+new String(err));
		
	}

	@Override
	public void okResponse(byte[] ok, MySQLConnection conn) {
		logger.info("received ok resp: "+conn+" "+new String(ok));
		
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, MySQLConnection conn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowResponse(byte[] row, MySQLConnection conn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rowEofResponse(byte[] eof, MySQLConnection conn) {
		// TODO Auto-generated method stub
		
	}

}