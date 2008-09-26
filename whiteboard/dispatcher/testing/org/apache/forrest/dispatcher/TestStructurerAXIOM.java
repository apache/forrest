package org.apache.forrest.dispatcher;

import java.io.InputStream;

import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.ClassPathResolver;
import org.apache.forrest.dispatcher.impl.XMLStructurerAxiom;

import junit.framework.TestCase;

public class TestStructurerAXIOM extends TestCase {
  private static final String STRUCTURER_XML = "master.advanced.structurer.xml";

  public void testStructurer() throws DispatcherException {
    String format = "html";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }
/*
  public void testStructurerWithXmlProperties() throws DispatcherException {
    String format = "fo";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }

  public void testStructurerXmlFormat() throws DispatcherException {
    String format = "xml";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }
*/
  private Structurer prepareStructurer(boolean allowXml) {
    DispatcherBean config = new DispatcherBean();
    config.setAllowXmlProperties(allowXml);
    config.setResolver(new ClassPathResolver());
    config.setContractUriPrefix("/org/apache/forrest/dispatcher/");
    Structurer structurer = new XMLStructurerAxiom(config);
    return structurer;
  }

  private InputStream getStream() {
    InputStream dataStream = this.getClass()
        .getResourceAsStream(STRUCTURER_XML);
    return dataStream;
  }
}
