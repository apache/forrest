package org.apache.forrest.http.client;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestPostFile {
    public TestPostFile(String destinationUrl, String srcUrl) throws MalformedURLException, IOException{
        PostFile post = new PostFile(destinationUrl, srcUrl);
        System.out.println("body: "+post.getResponseBodyAsString());
    }
    public static void main (String[] args) throws MalformedURLException, IOException{
        TestPostFile test = new TestPostFile(args[0],args[1]);
    }
}
