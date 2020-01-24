/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.datasource.pooled;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * This is a simple, synchronous, thread-safe database connection pool.
 *
 * @author Clinton Begin
 */
public class PooledDataSource implements DataSource {
	private static final String CONNECTION = "Connection";

    private static final Log log = LogFactory.getLog(PooledDataSource.class);


    private final PoolState state = new PoolState(this);

    private final UnpooledDataSource dataSource;
  
   

    // OPTIONAL CONFIGURATION FIELDS---å�¯é€‰é…�ç½®å­—æ®µ

    //æœ€å¤§æ´»è·ƒçš„è¿žæŽ¥æ•°
    protected int poolMaximumActiveConnections = 10;
    //æœ€å¤§é—²ç½®è¿žæŽ¥æ•°
    protected int poolMaximumIdleConnections = 5;
    //æœ€å¤§çš„æ£€æŸ¥è¿žæŽ¥æ—¶é—´
    protected int poolMaximumCheckoutTime = 20000;
    protected int poolTimeToWait = 20000;
    protected int poolMaximumLocalBadConnectionTolerance = 3;
    protected String poolPingQuery = "NO PING QUERY SET";
    protected boolean poolPingEnabled;
    protected int poolPingConnectionsNotUsedFor;

    private int expectedConnectionTypeCode;

    //æž„é€ å™¨ï¼ŒèŽ·å�–æ•°æ�®åº“è¿žæŽ¥æ•°æ�®æº�
    public PooledDataSource() {
        dataSource = new UnpooledDataSource();
    }

    public PooledDataSource(UnpooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param driver   æ•°æ�®åº“é©±åŠ¨ç±»
     * @param url      æ•°æ�®åº“è¿žæŽ¥url
     * @param username ç”¨æˆ·å��
     * @param password å¯†ç �
     */
    public PooledDataSource(String driver, String url, String username, String password) {
        dataSource = new UnpooledDataSource(driver, url, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
    }

    public PooledDataSource(String driver, String url, Properties driverProperties) {
        dataSource = new UnpooledDataSource(driver, url, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
    }

    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        dataSource = new UnpooledDataSource(driverClassLoader, driver, url, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
    }

    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        dataSource = new UnpooledDataSource(driverClassLoader, driver, url, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
    }

    //èŽ·å�–Connectionä»£ç�†æ•°æ�®åº“è¿žæŽ¥å¯¹è±¡
    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

 
    public void setLoginTimeout(int loginTimeout) {
     dataSource.setLoginTimeout(loginTimeout);
     forceCloseAll();
    
    }

   
    public int getLoginTimeout() {
       return  dataSource.getLoginTimeout();
       
    }

 
    public void setLogWriter(PrintWriter logWriter) {
        dataSource.setLogWriter(logWriter);
      
       forceCloseAll();
    }

   
    public PrintWriter getLogWriter() {
       return dataSource.getLogWriter();
       
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        dataSource.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
        forceCloseAll();
    }

    public void setDriverProperties(Properties driverProps) {
        dataSource.setDriverProperties(driverProps);
        forceCloseAll();
    }

    /**
     * The maximum number of active connections
     *
     * @param poolMaximumActiveConnections The maximum number of active connections
     */
    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
        forceCloseAll();
    }

    /**
     * The maximum number of idle connections
     *
     * @param poolMaximumIdleConnections The maximum number of idle connections
     */
    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
        forceCloseAll();
    }

    /**
     * The maximum number of tolerance for bad connection happens in one thread
     * which are applying for new {@link PooledConnection}
     *
     * @param poolMaximumLocalBadConnectionTolerance max tolerance for bad connection happens in one thread
     * @since 3.4.5
     */
    public void setPoolMaximumLocalBadConnectionTolerance(
            int poolMaximumLocalBadConnectionTolerance) {
        this.poolMaximumLocalBadConnectionTolerance = poolMaximumLocalBadConnectionTolerance;
    }

    /**
     * The maximum time a connection can be used before it *may* be
     * given away again.
     *
     * @param poolMaximumCheckoutTime The maximum time
     */
    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
        forceCloseAll();
    }

    /**
     * The time to wait before retrying to get a connection
     *
     * @param poolTimeToWait The time to wait
     */
    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
        forceCloseAll();
    }

    /**
     * The query to be used to check a connection
     *
     * @param poolPingQuery The query
     */
    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
        forceCloseAll();
    }

    /**
     * Determines if the ping query should be used.
     *
     * @param poolPingEnabled True if we need to check a connection before using it
     */
    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
        forceCloseAll();
    }

    /**
     * If a connection has not been used in this many milliseconds, ping the
     * database to make sure the connection is still good.
     *
     * @param milliseconds the number of milliseconds of inactivity that will trigger a ping
     */
    public void setPoolPingConnectionsNotUsedFor(int milliseconds) {
        this.poolPingConnectionsNotUsedFor = milliseconds;
        forceCloseAll();
    }

    public String getDriver() {
        return dataSource.getDriver();
    }

    public String getUrl() {
        return dataSource.getUrl();
    }

    public String getUsername() {
        return dataSource.getUsername();
    }

    public String getPassword() {
        return dataSource.getPassword();
    }

    public boolean isAutoCommit() {
        return dataSource.isAutoCommit();
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return dataSource.getDefaultTransactionIsolationLevel();
    }

    public Properties getDriverProperties() {
        return dataSource.getDriverProperties();
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public int getPoolMaximumLocalBadConnectionTolerance() {
        return poolMaximumLocalBadConnectionTolerance;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    /*
     * Closes all active and idle connections in the pool
     */
    public void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            for (int i = state.activeConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            for (int i = state.idleConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    public PoolState getPoolState() {
        return state;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    protected void pushConnection(PooledConnection conn) throws SQLException {

        synchronized (state) {
            state.activeConnections.remove(conn);
            if (conn.isValid()) {
                if (state.idleConnections.size() < poolMaximumIdleConnections && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    state.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
                    state.idleConnections.add(newConn);
                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                    conn.invalidate();
                    pushConnectionIf(newConn);
                    state.notifyAll();
                } else {
                    state.accumulatedCheckoutTime += conn.getCheckoutTime();
                    pushConnectionIf2(conn);
                    conn.getRealConnection().close();
                    pushConnectionIf3(conn);
                    conn.invalidate();
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("A bad connection (" + conn.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                }
                state.badConnectionCount++;
            }
        }
    }
    protected void pushConnectionIf(PooledConnection newConn){
        if (log.isDebugEnabled()) {
            log.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
        }
    }
    protected void pushConnectionIf2(PooledConnection conn) throws SQLException{
        if (!conn.getRealConnection().getAutoCommit()) {
            conn.getRealConnection().rollback();
        }
    }
    protected void pushConnectionIf3(PooledConnection conn) {
        if (log.isDebugEnabled()) {
            log.debug("Closed connection " + conn.getRealHashCode() + ".");
        }
    }

    private PooledConnection popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (state) {
                if (!state.idleConnections.isEmpty()) {

                    conn = state.idleConnections.remove(0);
                    popConnectionSupport(conn);

                } else {
                    // Pool does not have available connection
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {

                        conn = new PooledConnection(dataSource.getConnection(), this);
                      popConnectionSupportTwo(conn);

                    } else {
                        // Cannot create new connection
                        PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {

                          popConnectionSupportFour(conn, oldestActiveConnection, longestCheckoutTime);
                          conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                          privateSupportFour(conn, oldestActiveConnection);

                        } else {
                            // Must wait
                            try {
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                }
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;

                            }

                        }
                    }
                }
                if (conn != null) {
                    // ping to server and check the connection is valid or not
                    if (conn.isValid()) {

                      conn=popConnectionSupportThree(conn, username,password,t);

                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        }
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        conn = null;
                        if (localBadConnectionCount > (poolMaximumIdleConnections + poolMaximumLocalBadConnectionTolerance)) {
                            if (log.isDebugEnabled()) {
                                log.debug("PooledDataSource: Could not get a good connection to the database.");
                            }
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }

        }

        if (conn == null) {
            if (log.isDebugEnabled()) {
                log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            }
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;
    }

    private void popConnectionSupport(PooledConnection conn){
      if (log.isDebugEnabled()) {
          log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
      }
      
    }

    private void popConnectionSupportTwo(PooledConnection conn)throws SQLException{
      if (log.isDebugEnabled()) {
          log.debug("Created connection " + conn.getRealHashCode() + ".");
      }
    }

    private PooledConnection popConnectionSupportThree(PooledConnection conn,String username, String password, long t)throws SQLException{
      if (!conn.getRealConnection().getAutoCommit()) {
          conn.getRealConnection().rollback();
      }
      conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
      conn.setCheckoutTimestamp(System.currentTimeMillis());
      conn.setLastUsedTimestamp(System.currentTimeMillis());
      state.activeConnections.add(conn);
      state.requestCount++;
      state.accumulatedRequestTime += System.currentTimeMillis() - t;
      return conn;
    }

    private void popConnectionSupportFour(PooledConnection conn, PooledConnection oldestActiveConnection, long longestCheckoutTime)throws SQLException{
      // Can claim overdue connection
      state.claimedOverdueConnectionCount++;
      state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
      state.accumulatedCheckoutTime += longestCheckoutTime;
      state.activeConnections.remove(oldestActiveConnection);
      if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
          try {
              oldestActiveConnection.getRealConnection().rollback();
          } catch (SQLException e) {

              log.debug("Bad connection. Could not roll back");
          }
      }
      
     
     
    }

    private void privateSupportFour(PooledConnection conn, PooledConnection oldestActiveConnection){
        conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
        conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
        oldestActiveConnection.invalidate();
        if (log.isDebugEnabled()) {
            log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
        }
    }

    /**
     * Method to check to see if a connection is still usable
     *
     * @param conn - the connection to check
     * @return True if the connection is still usable
     */
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;

        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug(CONNECTION + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            }
            result = false;
        }

        if (poolPingEnabled && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug("Testing connection " + conn.getRealHashCode() + " ...");
                        }
                        Connection realConn = conn.getRealConnection();
                        /**
                         * try(){}:è¿™ç§�ç§°ä¸ºtry-with-resourcesè¯­å�¥ï¼Œtryè¯­å�¥åœ¨è¯¥è¯­å�¥ç»“æ�Ÿæ—¶è‡ªåŠ¨å…³é—­è¿™äº›èµ„æº�ã€‚
                         * æ­¤å¤„çš„èµ„æº�æŒ‡å¾—æ˜¯é‚£äº›å¿…é¡»åœ¨ç¨‹åº�ç»“æ�Ÿæ—¶å¿…é¡»å…³é—­çš„èµ„æº�ï¼ˆæ¯”å¦‚æ•°æ�®åº“è¿žæŽ¥ï¼Œç½‘ç»œè¿žæŽ¥ç­‰ï¼‰ã€‚
                         * try(){}æ˜¯jdk7å¼€å§‹çš„è¯­æ³•ã€‚
                         */
                        try (Statement statement = realConn.createStatement()) {
                            statement.executeQuery(poolPingQuery).close();
                        }

                        if (!realConn.getAutoCommit()) {
                            realConn.rollback();
                        }
                        result = true;
                        if (log.isDebugEnabled()) {
                            log.debug(CONNECTION + conn.getRealHashCode() + " is GOOD!");
                        }
                    } catch (Exception e) {
                        log.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (Exception e2) {
                            //ignore
                        }
                        result = false;
                        if (log.isDebugEnabled()) {
                            log.debug(CONNECTION + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                        }
                    }


        }
        return result;
    }

    /**
     * Unwraps a pooled connection to get to the 'real' connection
     *
     * @param conn - the pooled connection to unwrap
     * @return The 'real' connection
     */
    public static Connection unwrapConnection(Connection conn) {
        if (Proxy.isProxyClass(conn.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if (handler instanceof PooledConnection) {
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        return conn;
    }

    protected void finalized() throws Throwable {
        forceCloseAll();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // requires JDK version 1.6
    }

}
