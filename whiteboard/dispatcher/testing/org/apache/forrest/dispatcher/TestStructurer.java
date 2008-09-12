package org.apache.forrest.dispatcher;

import java.io.InputStream;

import org.apache.forrest.dispatcher.impl.XMLStructurer;

import junit.framework.TestCase;

public class TestStructurer extends TestCase {
  private static final String STRUCTURER_XML = "master.structurer.xml";
  public void testStructurer() throws DispatcherException{
    InputStream dataStream=this.getClass().getResourceAsStream(STRUCTURER_XML); 
    String format="html";
    XMLStructurer structurer  = new XMLStructurer(dataStream, format);
    structurer.setContractUriPrefix("/org/apache/forrest/dispatcher/");
    structurer.execute();
  }
}
