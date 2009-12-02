package org.apache.forrest.dispatcher;



import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.XMLStructurer;


public class TestStructurer extends AbstractStructurer {

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

  public void testStructurerXmlFormat() throws DispatcherException {
    String format = "xml";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }

  @Override
  public String getUrl() {
    return "master.structurer.xml";
  }

  @Override
  protected Structurer getStructurer(boolean allowXml) {
    super.prepareStructurer(allowXml);
    Structurer structurer = new XMLStructurer(config,properties);
    return structurer;
  }
}
