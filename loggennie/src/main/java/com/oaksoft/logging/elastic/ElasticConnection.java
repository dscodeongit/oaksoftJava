package com.oaksoft.logging.elastic;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.apache.logging.log4j.nosql.appender.NoSqlConnection;
import org.apache.logging.log4j.nosql.appender.NoSqlObject;
import org.apache.logging.log4j.status.StatusLogger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.oaksoft.util.datetime.DateTimeUtils;

public class ElasticConnection implements NoSqlConnection<Map<String, Object>, ElasticsearchObject> {
	
	private String host;
	private Integer port;
	private String indexPrefix;
	private String typePrefix;
	private CloseableHttpClient httpClient = HttpClients.createDefault();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private static final Logger logger = LogManager.getLogger();
    
    private static final String INDEX_PREFIX_DEFAULT = "monitor_";
    private static final String TYPE_PREFIX_DEFAULT = "statistics";


    /**
	 * Date format to use for the index name
	 */
//	private final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
	//private DateFormat dateFormatSDF;
	private DateTimeFormatter dateFormatSDF = DateTimeFormat.forPattern("yyyy.MM.dd");
	
	/**
	 * Elastic Search API URL template with place holders for host, port, date and type
	 */
//	private static final String ELASTIC_SEARCH_API_URI = "http://%s:%d/mule-%s/%s";
	private static final String ELASTIC_SEARCH_API_URI = "http://%s:%d/%s/%s";	
	
	/**
	 * Elastic Search BULK API URL template with place holders for host, port, date and type
	 */
	private static final String ELASTIC_SEARCH_BULK_API_URI = ELASTIC_SEARCH_API_URI + "/_bulk";
	
	/**
	 * Elastic Search BULK API create command
	 */
	private static final String ELASTIC_SEARCH_CREATE_BULK = "{ \"create\" : { } }\n";
	
	/**
	 * New Line
	 */
	private static final String NEW_LINE = "\n";
	
	public ElasticConnection(String host, Integer port){
		this.host = host;
		this.port = port;
	}	
	
	public ElasticConnection(String host, Integer port, String index, String type){
		this.host = host;
		this.port = port;
		this.indexPrefix = index;
		this.typePrefix = type;
	}	
	
	/**
	 * Upload single statistic to Elastic Search
	 * 
	 * @param stat The statistic to be uploaded
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void insertData(final Map<String, Object> dataMap) throws ClientProtocolException, IOException{
		
		logger.info("insert data item to Elastic Search");
		
		CloseableHttpResponse response = null;
		
		try
		{
			//convert to JSON
			
			
			String json = new Gson().toJson(dataMap);
			String srcLocation =  StringUtils.substringBefore(StringUtils.substringAfter(json, "sourceLocation\":\""), "\"");
			String index = ( indexPrefix == null ? INDEX_PREFIX_DEFAULT : indexPrefix+"_" ) + dateFormatSDF.print(DateTimeUtils.getJodaDateTimeUTC());
			String type = typePrefix == null ? (StringUtils.isNotBlank(srcLocation) ? srcLocation : TYPE_PREFIX_DEFAULT ) : typePrefix;
			
            logger.info("Insert data via Elasticsearch REST api: " + String.format(ELASTIC_SEARCH_API_URI, host, port, index, type));
			logger.debug("Data payload: " + json);
			//create and execute HTTP POST			
			
			HttpPost post = new HttpPost(String.format(ELASTIC_SEARCH_API_URI, host, port, index, type));
			StringEntity entity = new StringEntity(json);
			post.setEntity(entity);
			response = httpClient.execute(post);
		}
		catch(HttpHostConnectException e){
			logger.error("Elastic Search is unreacheable");
		}
		finally{
			if (response != null){
				if(response.getStatusLine().getStatusCode() >= 300){
					logger.error("failed to insert data to elasticsearch. reason: "+ response.getStatusLine());
				} else {
					logger.info("Successfully inserted Data to elasticsearch.");
				}
				response.close();
			}
		}
		
	}
	
	/**
	 * Upload a list of statistics to Elastic Search using the bulk upload API
	 */
	/*
	public void uploadBulkData(StatisticsType type, List<? extends AbstractCollectorStatistics> stats) throws ClientProtocolException, IOException{
		
		if(dateFormatSDF == null){
			dateFormatSDF = new SimpleDateFormat(dateformat);
		}
		
		logger.debug("Uploading bulk items to Elastic Search");
		
		CloseableHttpResponse response = null;
		
		try
		{
			//convert the list of statistics into a bulk request
			StringBuilder builder = new StringBuilder();
			
			//for each statistic, create a line with the create command
			//followed by the statistic itself
			for (AbstractCollectorStatistics stat : stats){
				
				//convert to JSON
				String json = statisitcsToJson.toJson(stat);
				
//logger.info("MYJSONDOC" + json);				
				
				
				//append create command
				builder.append(ELASTIC_SEARCH_CREATE_BULK);
				//append json representation of statistic
				builder.append(json + NEW_LINE);
			}
//logger.info("bulk Elastic search api uri " + String.format(ELASTIC_SEARCH_BULK_API_URI, host, port, index, dateFormatSDF.format(new Date()), type.toString()));
			
			//create and execute HTTP POST
			//HttpPost post = new HttpPost(String.format(ELASTIC_SEARCH_BULK_API_URI, host, port, dateFormatSDF.format(new Date()), type.toString()));
			HttpPost post = new HttpPost(String.format(ELASTIC_SEARCH_BULK_API_URI, host, port, index, dateFormatSDF.format(new Date()), type.toString()));
			
//logger.info("MYBULKJSONDOC " + builder.toString());
			StringEntity entity = new StringEntity(builder.toString());
			post.setEntity(entity);
			response = httpClient.execute(post);
		}
		catch(HttpHostConnectException e){
			logger.error("Elastic Search is unreacheable");
		}
		finally{
			if (response != null){
				response.close();
			}
		}
		
		logger.debug("Upload complete");
	}
	
	*/
	
	
	 @Override
	    public ElasticsearchObject createObject() {
	        return new ElasticsearchObject();
	    }

	    @Override
	    public ElasticsearchObject[] createList(final int length) {
	        return new ElasticsearchObject[length];
	    }

	    @Override
	    public void insertObject(final NoSqlObject<Map<String, Object>> object) {
	        try {
	        	insertData(object.unwrap());
	        } catch (Exception e) {
	            throw new AppenderLoggingException("failed to write log event to Elasticsearch: " + e.getMessage(), e);
	        }
	    }

	    @Override
	    public void close() {
	        if (closed.compareAndSet(false, true)) {
	            try {
					httpClient.close();
				} catch (IOException e) {
					logger.error("Error closing httpclient. caused by:  ", e);
				}
	        }
	    }

	    @Override
	    public boolean isClosed() {
	        return closed.get();
	    }


}
