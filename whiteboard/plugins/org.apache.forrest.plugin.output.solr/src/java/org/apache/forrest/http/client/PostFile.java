package org.apache.forrest.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class PostFile {
    /**
     * The AGENT name
     */
    public static final String AGENT = "forrest[" + PostFile.class.getName() + "]";

    private String destinationUrl, srcUrl;

    private HttpClient client;

    private PostMethod filePost;

    private int statusCode;

    /**
     * @param destinationUrl - the url of the server listener (e.g. servlet)
     * @param srcUrl - the src url of the file to post
     * @throws MalformedURLException
     * @throws IOException
     */
    public PostFile(String destinationUrl, String srcUrl) throws MalformedURLException,
            IOException {
        this.destinationUrl = destinationUrl;
        this.srcUrl = srcUrl;
        client = new HttpClient();
        filePost = prepareFilePost(destinationUrl, srcUrl); 
        statusCode = client.executeMethod(filePost);
    }
    
    public void post(String destinationUrl, String srcUrl) throws MalformedURLException, IOException{
        this.destinationUrl = destinationUrl;
        this.srcUrl = srcUrl;
        client = new HttpClient();
        filePost = prepareFilePost(destinationUrl, srcUrl); 
        statusCode = client.executeMethod(filePost);
    }
    public int statusCode(){
        return statusCode;
    }
    public String getResponseCharSet(){
        return filePost.getResponseCharSet();
    }
    public String getResponseBodyAsString(){
        return filePost.getResponseBodyAsString();
    }
    public InputStream getResponseBodyAsStream()
            throws MalformedURLException, IOException {
        return filePost.getResponseBodyAsStream();
    }

    private PostMethod prepareFilePost(String solrBase, String src)
            throws IOException, MalformedURLException {
        PostMethod filePost = new PostMethod(solrBase);
        filePost.addRequestHeader("Content-type", "text/xml; charset=utf-8");
        filePost.addRequestHeader("User-Agent", AGENT);
        filePost.setRequestBody(new URL(src).openStream());
        return filePost;
    }
    public String getsolrBase() {
        return destinationUrl;
    }
    public String getSrc() {
        return srcUrl;
    }

}
