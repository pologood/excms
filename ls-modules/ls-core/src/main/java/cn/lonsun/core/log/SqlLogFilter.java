package cn.lonsun.core.log;

import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * druid sql打印过滤器
 * 配置说明： statementLogEnabled = true 时 打印成功sql
 *          statementLogErrorEnabled = true 时 打印异常sql
 * @auth zhongjun
 * @createDate 2018-07-11 20:07
 */
public class SqlLogFilter extends LogFilter {

    private Logger statementLogger  = LoggerFactory.getLogger("sqlLog");

    private boolean statementExecuteAfterLogEnable = true;

    /**
     * 执行耗时毫秒数，默认为0,如果大于0 则只打印执行时间大于该值的sql
     */
    private Long executeTimeMillis = 0L;

    public boolean isStatementExecuteAfterLogEnable() {
        return statementExecuteAfterLogEnable;
    }

    public void setStatementExecuteAfterLogEnable(boolean statementExecuteAfterLogEnable) {
        this.statementExecuteAfterLogEnable = statementExecuteAfterLogEnable;
    }

    public Long getExecuteTimeMillis() {
        return executeTimeMillis;
    }

    public void setExecuteTimeMillis(Long executeTimeMillis) {
        this.executeTimeMillis = executeTimeMillis;
    }

    @Override
    public String getDataSourceLoggerName() {
        return dataSourceLoggerName;
    }

    @Override
    public void setDataSourceLoggerName(String dataSourceLoggerName) {
        this.dataSourceLoggerName = dataSourceLoggerName;
        statementLogger = LoggerFactory.getLogger(dataSourceLoggerName);
    }

    public void setDataSourceLogger(Logger dataSourceLogger) {
        this.statementLogger = dataSourceLogger;
        this.dataSourceLoggerName = dataSourceLogger.getName();
    }

    @Override
    public String getConnectionLoggerName() {
        return connectionLoggerName;
    }

    @Override
    public void setConnectionLoggerName(String connectionLoggerName) {
        this.connectionLoggerName = connectionLoggerName;
        statementLogger = LoggerFactory.getLogger(connectionLoggerName);
    }

    public void setConnectionLogger(Logger connectionLogger) {
        this.statementLogger = connectionLogger;
        this.connectionLoggerName = connectionLogger.getName();
    }

    @Override
    public String getStatementLoggerName() {
        return statementLoggerName;
    }

    @Override
    public void setStatementLoggerName(String statementLoggerName) {
        this.statementLoggerName = statementLoggerName;
        statementLogger = LoggerFactory.getLogger(statementLoggerName);
    }

    public void setStatementLogger(Logger statementLogger) {
        this.statementLogger = statementLogger;
        this.statementLoggerName = statementLogger.getName();
    }

    @Override
    public String getResultSetLoggerName() {
        return resultSetLoggerName;
    }

    @Override
    public void setResultSetLoggerName(String resultSetLoggerName) {
        this.resultSetLoggerName = resultSetLoggerName;
        statementLogger = LoggerFactory.getLogger(resultSetLoggerName);
    }

    public void setResultSetLogger(Logger resultSetLogger) {
        this.statementLogger = resultSetLogger;
        this.resultSetLoggerName = resultSetLogger.getName();
    }

    @Override
    public boolean isConnectionLogErrorEnabled() {
        return statementLogger.isErrorEnabled() && super.isConnectionLogErrorEnabled();
    }

    @Override
    public boolean isDataSourceLogEnabled() {
        return statementLogger.isDebugEnabled() && super.isDataSourceLogEnabled();
    }

    @Override
    public boolean isConnectionLogEnabled() {
        return statementLogger.isDebugEnabled() && super.isConnectionLogEnabled();
    }

    @Override
    public boolean isStatementLogEnabled() {
        return statementLogger.isDebugEnabled() && super.isStatementLogEnabled();
    }

    @Override
    public boolean isResultSetLogEnabled() {
        return statementLogger.isDebugEnabled() && super.isResultSetLogEnabled();
    }

    @Override
    public boolean isResultSetLogErrorEnabled() {
        return statementLogger.isErrorEnabled() && super.isResultSetLogErrorEnabled();
    }

    @Override
    public boolean isStatementLogErrorEnabled() {
        return statementLogger.isErrorEnabled() && super.isStatementLogErrorEnabled();
    }

    @Override
    protected void connectionLog(String message) {
        if(super.isConnectionLogEnabled()){
            statementLogger.debug(message);
        }
    }

    @Override
    protected void statementLog(String message) {
        if(super.isStatementLogEnabled()){
            statementLogger.debug(message);
        }
    }

    @Override
    protected void resultSetLog(String message) {
        if(super.isResultSetLogEnabled()){
            statementLogger.debug(message);
        }
    }

    @Override
    protected void resultSetLogError(String message, Throwable error) {
        statementLogger.error(message, error);
    }

    @Override
    protected void statementLogError(String message, Throwable error) {
        if(super.isResultSetLogErrorEnabled()){
            statementLogger.error(message, error);
        }
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        if (this.isStatementLogErrorEnabled()) {
            this.statementLogError(formatSql(statement, sql), error);
        }
    }

    /**
     * 格式化sql, 参考父类 logExecutableSql(StatementProxy statement, String sql)
     * @param statement
     * @param sql
     * @return
     */
    private String formatSql(StatementProxy statement, String sql) {
        int parametersSize = statement.getParametersSize();
        if (parametersSize == 0) {
            return sql;
        }

        List<Object> parameters = new ArrayList<Object>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            JdbcParameter jdbcParam = statement.getParameter(i);
            Object value = jdbcParam.getValue();
            if(value instanceof String && ((String) value).length() > 100){
                value = ((String)value).substring(0, 50)
                        + "[more"+ (((String) value).length() - 50) +"]";
            }
            parameters.add(value);
        }
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String formattedSql = SQLUtils.format(sql, dbType, parameters);
        return formattedSql.replaceAll("\n"," ").replaceAll("\t"," ");
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        if (this.isStatementExecuteAfterLogEnable()) {
            statement.setLastExecuteTimeNano();
            double nanos = statement.getLastExecuteTimeNano();
            double millis = nanos / (1000 * 1000);
            printLogWithTime(formatSql(statement, sql), millis);
        }
    }

    private void printLogWithTime(String sql, double millis) {
        if(executeTimeMillis <= 0 || millis > executeTimeMillis){
            statementLogger.info("Execute in({}ms). {}",
                    new BigDecimal(millis).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), sql);
        }
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        String sql;
        if (statement instanceof PreparedStatementProxy) {
            sql = ((PreparedStatementProxy) statement).getSql();
        } else {
            sql = statement.getBatchSql();
        }
        statementExecuteAfter(statement, sql, true);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        statementExecuteAfter(statement, sql, true);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        statementExecuteAfter(statement, sql, true);
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
        super.resultSetOpenAfter(resultSet);
    }

    @Override
    protected void statementCreateAfter(StatementProxy statement) {
        super.statementCreateAfter(statement);
    }

    @Override
    protected void statementPrepareAfter(PreparedStatementProxy statement) {
        super.statementPrepareAfter(statement);
    }
}
