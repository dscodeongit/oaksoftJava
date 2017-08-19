package com.oaksoft.logging.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oaksoft.logging.config.DataSnap;
import com.oaksoft.logging.config.Monitor;

public class JdbcMonitor implements Monitor{	
	private static Logger logger = LogManager.getLogger();
	protected static final String[] ARRAY_TEMP = new String[0];
	protected String name;
	protected String type;
	private Set<String> queries;
	
	public JdbcMonitor(String name, String type, Set<String> queryList) {
		this.name = name;
		this.type = type;
		this.queries = Sets.newHashSet(queryList);
	}		
	
	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}
		
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(queries);
	}

	@Override
	public Collection<DataSnap> snap(Object connectionObject) throws SQLException {
		Connection conn = (Connection)connectionObject;
		Statement statement = null;
		ResultSet resultSet = null;
		Collection<DataSnap> snaps = Lists.newArrayList();
		try {
			if(CollectionUtils.isNotEmpty(queries)){
				for (String query : queries) {					
					logger.debug("Executing query { " + query + " }");
					final long start = System.currentTimeMillis();					
					statement = conn.createStatement();
					resultSet = statement.executeQuery(query);
					logger.debug("Fetching results of query { " + query + " }");
					Map<String, Object> dataMap = Maps.newHashMap();
					while (resultSet.next()) {
						final int columnCount = resultSet.getMetaData().getColumnCount();
						
						for (int ind = 0; ind < columnCount; ind++ ) {
							String colName = resultSet.getMetaData().getColumnLabel(ind + 1);
							Object value = resultSet.getObject(colName);
							dataMap.put(colName, value);
						}						
						JdbcDataSnap snap = new JdbcDataSnap(this.name, this.type, dataMap);
						snaps.add(snap);
					}					
					final long end = System.currentTimeMillis();
					logger.debug("time used for query { " + query + " }" + (end - start) + " ms");
				}
			}
		} finally {
			try {
				if(resultSet != null ){
					resultSet.close();
				}
				if(statement != null ){
					statement.close();					
				}
			} catch (final SQLException e) {
				// ignore
			}
		}		
		
		return snaps;						
	}		

}
