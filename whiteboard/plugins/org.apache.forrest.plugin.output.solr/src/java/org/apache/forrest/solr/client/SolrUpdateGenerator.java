package org.apache.forrest.solr.client;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.forrest.http.client.PostFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SolrUpdateGenerator extends ServiceableGenerator {
    
    private static final String DESTINATION_URL = "destinationUrl";

    private static final String SOLR_UPDATE_URL = "http://localhost:8983/solr/update";

    private PostFile post;

    private SAXParser parser;

    private String destination;

    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        destination = par.getParameter(DESTINATION_URL, SOLR_UPDATE_URL);
    }

    public void generate() throws IOException, SAXException, ProcessingException {
        Source inputSource = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            inputSource = resolver.resolveURI(this.source);
            if (inputSource.exists()) {
                post = new PostFile(destination, inputSource.getInputStream());
                // Set up parser to parse the input file
                parser = (SAXParser) manager.lookup(SAXParser.ROLE);
                parser.parse(new InputSource(post.getResponseBodyAsStream()),contentHandler);
            }else{
                throw new ProcessingException("source: "+this.source+" does not exits");
            }
            
        } catch (ServiceException e) {
            throw new ProcessingException(e);
        }
        
    }

}
