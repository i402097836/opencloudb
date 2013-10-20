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
 * (created at 2012-4-17)
 */
package org.opencloudb.mysql.nio;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.config.Alarms;
import org.opencloudb.config.model.DataSourceConfig;
import org.opencloudb.heartbeat.MySQLHeartbeat;
import org.opencloudb.mysql.MySQLDataNode;
import org.opencloudb.mysql.nio.handler.DelegateResponseHandler;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.statistic.SQLRecorder;
import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public class MySQLDataSource {
	private static final Logger alarm = Logger.getLogger("alarm");

	private final MySQLDataNode dataNode;
	private final int index;
	private final String name;
	private final ReentrantLock lock = new ReentrantLock();
	private final MySQLConnectionFactory factory;
	private final DataSourceConfig config;
	private final int size;

	private final MySQLConnection[] items;
	private int activeCount;
	private int idleCount;
	private final MySQLHeartbeat heartbeat;

	private final SQLRecorder sqlRecorder;

	public MySQLDataSource(MySQLDataNode node, int index,
			DataSourceConfig config, int size) {
		this.dataNode = node;
		this.size = size;
		this.items = new MySQLConnection[size];
		this.config = config;
		this.name = config.getName();
		this.index = index;
		this.factory = new MySQLConnectionFactory();
		this.sqlRecorder = new SQLRecorder(config.getSqlRecordCount());
		this.heartbeat = new MySQLHeartbeat(this);
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}
	

//   public MySQLConnection getConnection()
//   {
//	   return this.items[this.index];
//   }
	public MySQLDataNode getNode() {
		return dataNode;
	}
	 public int size() {
	        return size;
	    }
	public int getActiveCount() {
		return activeCount;
	}

	public MySQLHeartbeat getHeartbeat() {
		return heartbeat;
	}

	public int getIdleCount() {
		return idleCount;
	}
	public void idleCheck(long timeout) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final MySQLConnection[] items = this.items;
            long time = TimeUtil.currentTimeMillis() - timeout;
            for (int i = 0; i < items.length; i++) {
            	MySQLConnection c = items[i];
                if (c != null && time > c.getLastTime()) {
                    c.closeNoActive();
                    --idleCount;
                    items[i] = null;
                }
            }
        } finally {
            lock.unlock();
        }
    }
	public void clear() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final MySQLConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				MySQLConnection c = items[i];
				if (c != null) {
					c.closeNoActive();
					--idleCount;
					items[i] = null;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void startHeartbeat() {
		heartbeat.start();
	}

	public void stopHeartbeat() {
		heartbeat.stop();
	}

	public void doHeartbeat() {
		if (!heartbeat.isStop()) {
			try {
				heartbeat.heartbeat();
			} catch (Throwable e) {
				alarm.error(name + " heartbeat error.", e);
			}
		}
	}

	
	public MySQLConnection getConnection(final ResponseHandler handler,
			final Object attachment) throws Exception {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			// too many active connections
			if (activeCount >= size) {
				StringBuilder s = new StringBuilder();
				s.append(Alarms.DEFAULT).append("[name=").append(name)
						.append(",active=");
				s.append(activeCount).append(",size=").append(size).append(']');
				alarm.error(s.toString());
			}

			// get connection from pool
			final MySQLConnection[] items = this.items;
			for (int i = 0, len = items.length; idleCount > 0 && i < len; ++i) {
				if (items[i] != null) {
					MySQLConnection conn = items[i];
					items[i] = null;
					--idleCount;
					if (conn.isClosedOrQuit()) {
						continue;
					} else {
						++activeCount;
						conn.setAttachment(attachment);
						handler.connectionAcquired(conn);
						return conn;
					}
				}
			}

			++activeCount;
		} finally {
			lock.unlock();
		}

		// create connection
		return factory.make(this, new DelegateResponseHandler(handler) {
			private boolean deactived;

			@Override
			public void connectionError(Throwable e, MySQLConnection conn) {
				lock.lock();
				try {
					if (!deactived) {
						--activeCount;
						deactived = true;
					}
				} finally {
					lock.unlock();
				}
				handler.connectionError(e, conn);
			}

			@Override
			public void connectionAcquired(MySQLConnection conn) {
				conn.setAttachment(attachment);
				handler.connectionAcquired(conn);
			}
		});
	}

	public void releaseChannel(MySQLConnection c) {
		if (c == null || c.isClosedOrQuit()) {
			return;
		}

		// release connection
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final MySQLConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				if (items[i] == null) {
					++idleCount;
					--activeCount;
					c.setLastTime(TimeUtil.currentTimeMillis());
					items[i] = c;
					return;
				}
			}
		} finally {
			lock.unlock();
		}

		// close excess connection
		c.quit();
	}

	public void deActive() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			--activeCount;
		} finally {
			lock.unlock();
		}
	}

	public SQLRecorder getSqlRecorder() {
		return sqlRecorder;
	}

	public DataSourceConfig getConfig() {
		return config;
	}

}