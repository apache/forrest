package org.apache.forrest.dispatcher;

import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.XMLStructurerAxiom;

public class TestStructurerAXIOM  extends AbstractStructurer {
  @Override
  public String getUrl() {
    return "master.advanced.structurer.xml";
  }
  
  public void testStructurer() throws DispatcherException {
    String format = "html";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerWithXmlProperties() throws DispatcherException {
    String format = "html";
    Structurer structurer = getStructurer(true);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerFo() throws DispatcherException {
    String format = "fo";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerXmlFormat() throws DispatcherException {
    String format = "xml";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  @Override
  protected Structurer getStructurer(boolean allowXml) {
    super.prepareStructurer(allowXml);
    Structurer structurer = new XMLStructurerAxiom(config,properties);
    return structurer;
  }
}
