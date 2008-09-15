package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.forrest.dispatcher.DispatcherException;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.helper.StAX;

public class XMLStructurer extends StAX {

  public static final String NS = "http://apache.org/forrest/templates/2.0";

  public static final String STRUCTURER_ELEMENT = "structurer";

  public static final String STRUCTURE_ELEMENT = "structure";

  public static final String TYPE_ATT = "type";

  public static final String HOOKS_ATT = "hooksXpath";

  public static final String CONTRACT_ELEMENT = "contract";

  public static final String NAME_ATT = "name";

  public static final String DATA_ATT = "dataURI";

  public static final String PROPERTY_ELEMENT = "property";

  public static final String HOOK_ELEMENT = "hook";

  private static final Object VALUE_ATT = "value";

  private String format = "";
  private InputStream dataStream = null;

  private String currentPath = "";

  private boolean allowXmlProperties = false;

  public boolean isAllowXmlProperties() {
    return allowXmlProperties;
  }

  public void setAllowXmlProperties(boolean allowXmlProperties) {
    this.allowXmlProperties = allowXmlProperties;
  }

  private String contractUriPrefix = "";
  private String contractUriSufix = ".contract.xml";

  public String getContractUriPrefix() {
    return contractUriPrefix;
  }

  public void setContractUriPrefix(String contractUriPrefix) {
    this.contractUriPrefix = contractUriPrefix;
  }

  public String getContractUriSufix() {
    return contractUriSufix;
  }

  public void setContractUriSufix(String contractUriSufix) {
    this.contractUriSufix = contractUriSufix;
  }

  public XMLStructurer(InputStream dataStream, String format) {
    this.format = format;
    this.dataStream = dataStream;
  }

  public BufferedInputStream execute() throws DispatcherException {
    try {
      XMLStreamReader reader = getReader(dataStream);
      boolean process = true;
      while (process) {
        int event = reader.next();
        switch (event) {
        case XMLStreamConstants.END_DOCUMENT:
          process = false;
          break;

        case XMLStreamConstants.START_ELEMENT:
          String elementName = reader.getLocalName();
          if (elementName.equals(STRUCTURE_ELEMENT)) {
            String m_type = "", path = "";
            // Get attribute names
            for (int i = 0; i < reader.getAttributeCount(); i++) {
              String localName = reader.getAttributeLocalName(i);
              if (localName.equals(TYPE_ATT)) {
                // Return value
                m_type = reader.getAttributeValue(i);
              } else if (localName.equals(HOOKS_ATT)) {
                path = reader.getAttributeValue(i);
              }
            }
            if (m_type.equals(format)) {
              log.debug("matched - need to process");
              // adding the default path
              if (!"/".equals(String.valueOf(path.charAt(0)))) {
                path = "/" + path;
              }
              currentPath = path;
              processStructure(reader);
            } else {
              log.debug("no-matched");
            }
          }
          break;

        default:
          break;
        }
      }
    } catch (XMLStreamException e) {
      throw new DispatcherException(e);
    } catch (IOException e) {
      throw new DispatcherException(e);
    }
    return null;
  }

  private void processStructure(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(STRUCTURE_ELEMENT)) {
          // TODO: add logic here to produce the response
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(CONTRACT_ELEMENT)) {
          log.debug("Path " + currentPath);
          processContract(reader);
        } else if (elementName.equals(HOOK_ELEMENT)) {
          log.debug("HOOKS " + elementName);
          log.info("HOOKS transformation NOT YET IMPLEMENTED");
        }
        break;

      default:
        break;

      }
    }

  }

  private void processContract(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    String name = "", data = "";
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(NAME_ATT)) {
        // Return value
        name = reader.getAttributeValue(i);
      } else if (localName.equals(DATA_ATT)) {
        data = reader.getAttributeValue(i);
      }
    }
    /*
     * FIXME: TEMPORAL HACK ONLY Use source resolver/contract factory when
     * fixing this.
     * 
     * Ignoring dataStream completely for now
     * 
     * THIS ONLY WORKS FOR JUNIT ATM!!!
     */
    dataStream = null;
    Contract contract = new XSLContract(false);
    InputStream xslStream = this.getClass().getResourceAsStream(
        this.contractUriPrefix + name + this.contractUriSufix);
    contract.initializeFromStream(xslStream);
    // closing stream
    if(xslStream!=null){
      xslStream.close();
    }
    /*
     * HACK END
     */

    HashMap<String, ?> param = new HashMap();
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(CONTRACT_ELEMENT)) {
          InputStream resultStream = contract.execute(dataStream, param);
          if (null!=dataStream){
            dataStream.close();
          }
          // FIXME: add the stream to the result map with the actual path
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(PROPERTY_ELEMENT)) {
          processProperty(reader, param);
        }
        break;

      default:
        break;

      }
    }
  }

  private void processProperty(XMLStreamReader reader, HashMap param) {
    String propertyName = null, propertyValue = null;
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(NAME_ATT)) {
        // Return value
        propertyName = reader.getAttributeValue(i);
      } else if (localName.equals(VALUE_ATT)) {
        propertyValue = reader.getAttributeValue(i);
      }
    }
    if (allowXmlProperties) {
      // FIXME: record the events that are coming now
    } else if (null != propertyValue && null != propertyName) {
      param.put(propertyName, propertyValue);
    }

  }

}
