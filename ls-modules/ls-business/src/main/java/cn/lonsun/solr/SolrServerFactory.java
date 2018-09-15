package cn.lonsun.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * @author gu.fei
 * @version 2016-3-18 14:26
 */
public class SolrServerFactory {

    private static SolrServerFactory solrServer = null;
    private static HttpSolrServer server = null;
//    private static PropertiesSolr properties = SpringContextHolder.getBean("propertiesSolr");

    public static synchronized SolrServerFactory getInstance() {
        if (solrServer == null){
            solrServer = new SolrServerFactory();
        }
        return solrServer;
    }

    public static HttpSolrServer getServer(){
        try {
            if(server == null){
                server = new HttpSolrServer("http://192.168.1.206:9090/solr/");
                server.setSoTimeout(1000);  // socket read timeout
                server.setConnectionTimeout(1000);
                server.setDefaultMaxConnectionsPerHost(100);
                server.setMaxTotalConnections(100);
                server.setFollowRedirects(false);  // defaults to false
                server.setAllowCompression(true);
                server.setMaxRetries(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return server;
    }
}
