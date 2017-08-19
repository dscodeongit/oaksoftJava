package com.oaksoft.logging.elastic;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.nosql.appender.NoSqlProvider;
import org.apache.logging.log4j.status.StatusLogger;


@Plugin(name = "Elasticsearch", category = "Core", printObject = true)
public class ElasticsearchProvider implements NoSqlProvider<ElasticConnection> {

    private static final Logger logger = StatusLogger.getLogger();

    private final String host;
    private final Integer port;
    private final String index;
    private final String type;

    private final String description;

    private ElasticsearchProvider(final String host, final Integer port, final String index, final String type, final String description) {
    	this.host = host;
    	this.port = port;
    	this.index = index;
    	this.type = type;
        this.description = "elasticsearch{ " + description + " }";
    }

    @Override
    public ElasticConnection getConnection() {
        return new ElasticConnection(this.host, this.port, this.index, this.type);
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Factory method for creating an Elasticsearch provider within the plugin manager.
     *
     * @param cluster The name of the Elasticsearch cluster to which log event documents will be written.
     * @param host    The host name an Elasticsearch server node of the cluster, defaults to localhost.
     * @param port    The port that Elasticsearch is listening on, defaults to 9300
     * @param index   The index that Elasticsearch shall use for indexing
     * @param type    The type of the index Elasticsearch shall use for indexing
     * @return a new Elasticsearch provider
     */
    @PluginFactory
    public static ElasticsearchProvider createNoSqlProvider(
            @PluginAttribute("cluster") String cluster,
            @PluginAttribute("host") String host,
            @PluginAttribute("port") Integer port,
            @PluginAttribute("index") String index,
            @PluginAttribute("type") String type,
            @PluginAttribute("timeout") String timeout,
            @PluginAttribute("maxActionsPerBulkRequest") Integer maxActionsPerBulkRequest,
            @PluginAttribute("maxConcurrentBulkRequests") Integer maxConcurrentBulkRequests,
            @PluginAttribute("maxVolumePerBulkRequest") String maxVolumePerBulkRequest,
            @PluginAttribute("flushInterval") String flushInterval) {

        if (cluster == null || cluster.isEmpty()) {
            cluster = "elasticsearch";
        }
        if (host == null || host.isEmpty()) {
            host = "localhost";
        }
        if (port == null || port == 0) {
            port = 9200;
        }
        /*
        if (index == null || index.isEmpty()) {
            index = "emsmonitor";
        }
        if (type == null || type.isEmpty()) {
            type = "emsmonitor";
        }
        */
        if (timeout == null || timeout.isEmpty()) {
            timeout = "30s";
        }
        if (maxActionsPerBulkRequest == null) {
            maxActionsPerBulkRequest = 1000;
        }
        if (maxConcurrentBulkRequests == null) {
            maxConcurrentBulkRequests = 2 * Runtime.getRuntime().availableProcessors();
        }
        if (maxVolumePerBulkRequest == null || maxVolumePerBulkRequest.isEmpty()) {
            maxVolumePerBulkRequest = "10m";
        }
       
        String description = "default cluster=" + cluster + ",default host=" + host + ",default port=" + port;
        ElasticsearchProvider elasticsearchProvider = new ElasticsearchProvider(host, port, index, type, description);
        
        return elasticsearchProvider;
    }

}
