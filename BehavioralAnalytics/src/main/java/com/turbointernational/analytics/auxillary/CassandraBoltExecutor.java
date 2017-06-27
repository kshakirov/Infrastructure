package com.turbointernational.analytics.auxillary;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import org.apache.storm.tuple.Values;

/**
 * Created by kshakirov on 6/26/17.
 */
public class CassandraBoltExecutor {

    private Session session;
    private MappingManager manager;

    public CassandraBoltExecutor(Session session) {
        this.session = session;
        manager = new MappingManager(session);
    }

    public Values execute(Long primaryKey, String type) {
        String cqlQuery = "";
        Values values = null;
        if (BoltUtils.isVisitorLog(type)) {
            cqlQuery = creatCqlQuery(CassandraTable.VISITOR_LOG);
            ResultSet rs = queryDatabase(cqlQuery, primaryKey);
            values = createValues(type, getVisitorLogData(rs));
        }
        return values;
    }

    private String creatCqlQuery(CassandraTable cassandraTable) {
        return "Select * from " + cassandraTable.tableize() + " Where "
                + cassandraTable.primaryKey() + " = ? " +
                " LIMIT 1";
    }

    private ResultSet queryDatabase(String cqlQuery, Long primaryKey) {
        return session.execute(cqlQuery, primaryKey);
    }

    private VisitorLog getVisitorLogData(ResultSet rs) {
        Mapper<VisitorLog> mapper = manager.mapper(VisitorLog.class);
        Result<VisitorLog> visitor_logs = mapper.map(rs);
        return visitor_logs.one();
    }

    private Values createValues(String type, VisitorLog visitorLog) {
        return new Values(type, visitorLog);
    }
}
