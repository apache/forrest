package org.apache.forrest.solr.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.forrest.http.client.PostFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SolrUpdateGenerator extends ServiceableGenerator {
    private static final String SRC_ID = "srcId";

    private static final String SRC_URL_BASE = "srcUrlBase";

    private static final String SERVER = "http://localhost:8888/";

    private static final String DESTINATION_URL = "destinationUrl";

    private static final String SOLR_UPDATE_URL = "http://localhost:8983/solr/update";

    private PostFile post;

    private SAXParser parser;

    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
            try {
                init(par);
            } catch (ServiceException e) {
                throw  new ProcessingException (e);
            }
    }

    private void init(Parameters par) throws MalformedURLException, IOException,
             SAXException, ServiceException {
        String destination = par.getParameter(DESTINATION_URL, SOLR_UPDATE_URL);
        String base = par.getParameter(SRC_URL_BASE, SERVER);
        String srcId = par.getParameter(SRC_ID, null);
        String srcUrl = base + srcId;
        post = new PostFile(destination, srcUrl);
        // Set up to read the input file
        parser = (SAXParser) manager.lookup(SAXParser.ROLE);
    }

    public void generate() throws IOException, SAXException, ProcessingException {
        parser.parse(new InputSource(post.getResponseBodyAsStream()),contentHandler);
    }

}
