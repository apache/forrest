/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.forrest.forrestdoc.java.doc;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

/**
 * Main Doclet class to generate JavaDocXML.  This doclet generates the
 * document conforming to DTD specified in javadoc-v04draft.dtd.
 *
 * @version $Id: $
 */
public class XMLDoclet extends Doclet {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( XMLDoclet.class );

    private String xmlns = "jvx";
    private String encodingFormat = "ISO-8859-1";
    private String localName = "javadoc";
    private TransformerHandler handler = null;
    private String targetFileName = "simple.xml";
    private Attributes emptyAtts = new AttributesImpl();

    public XMLDoclet(RootDoc root) throws Exception {

        SAXTransformerFactory tFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        if (!(tFactory.getFeature(javax.xml.transform.sax.SAXSource.FEATURE) &&
                tFactory.getFeature(javax.xml.transform.stream.StreamResult.FEATURE))) {

            throw new Exception("The supplied TrAX transformer library is inadeguate." +
                    "Please upgrade to the latest version.");
        }

        File writer = new File(targetFileName);
        StreamResult result = new StreamResult(writer);

        // SAX2.0 ContentHandler.
        handler = tFactory.newTransformerHandler();

        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, encodingFormat);
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//APACHE//DTD JavaDoc V0.4//EN");
        serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "javadoc-v04draft.dtd");

        handler.setResult(result);

        javadocXML(root);
    }

    /**
     * Generates the xml data for the top element.
     * <xmp><!ELEMENT javadoc (package*, class*, interface*)></xmp>
     */
    private void javadocXML(RootDoc root) throws SAXException {

        handler.startDocument();

        handler.startElement(xmlns, localName, "javadoc", emptyAtts);
        PackageDoc[] packageArray = root.specifiedPackages();

        // Generate for packages.
        for (int i = 0; i < packageArray.length; ++i) {
            packageXML(packageArray[i]);
        }

        // Generate for classes.
        ClassDoc[] classArray = root.specifiedClasses();
        Vector interfaceVector = new Vector();
        for (int i = 0; i < classArray.length; ++i) {
            if (classArray[i].isInterface()) {
                interfaceVector.addElement(classArray[i]);
            } else {
                classXML(classArray[i]);
            }
        }

        // Generate for interfaces.
        Enumeration interfaceEnum = interfaceVector.elements();
        if (interfaceEnum.hasMoreElements()) {
            ClassDoc interfaceDoc = (ClassDoc) interfaceEnum.nextElement();
            interfaceXML(interfaceDoc);
        }
        handler.endElement(xmlns, localName, "javadoc");

        handler.endDocument();
    }

    /**
     * Generates doc for "package".
     * <xmp><!ELEMENT package (doc?, package*, class*, interface*)>
     * <!ATTLIST package
     * name CDATA #REQUIRED></xmp>
     */
    private void packageXML(PackageDoc packageDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", packageDoc.name());
        handler.startElement(xmlns, localName, "package", atts);

        // generate Doc element.
        docXML(packageDoc);

        // TODO:generate Package elements.
        // doubt: How package can exist inside a package?

        /* Generate for classes. */
        // for ordinary classes.
        ClassDoc[] classArray = packageDoc.ordinaryClasses();
        for (int i = 0; i < classArray.length; ++i) {
            classXML(classArray[i]);
        }
        // for Exception classes.
        classArray = packageDoc.exceptions();
        for (int i = 0; i < classArray.length; ++i) {
            classXML(classArray[i]);
        }
        // for Error classes
        classArray = packageDoc.errors();
        for (int i = 0; i < classArray.length; ++i) {
            classXML(classArray[i]);
        }

        /* Generate for interfaces. */
        ClassDoc[] interfaceArray = packageDoc.interfaces();
        for (int i = 0; i < interfaceArray.length; ++i) {
            interfaceXML(interfaceArray[i]);
        }

        handler.endElement(xmlns, localName, "package");
    }

    /**
     * Generates doc for element "class".
     * <xmp> <!ELEMENT class (doc?,
     * extends_class?,
     * implements?,
     * constructor*,
     * method*,
     * innerclass*)>
     * <!ATTLIST class
     * %name;
     * %extensibility;
     * %class.access;></xmp>
     */
    private void classXML(ClassDoc classDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", classDoc.name());

        String extensibility = "default";
        if (classDoc.isAbstract()) {
            extensibility = "abstract";
        } else if (classDoc.isFinal()) {
            extensibility = "final";
        }
        atts.addAttribute(xmlns, localName, "extensiblity", "String", extensibility);
        String access = "package";
        if (classDoc.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);
        handler.startElement(xmlns, localName, "class", atts);

        // generate "doc" sub-element
        docXML(classDoc);

        // generate "extends_class" sub-element
        extendsXML(classDoc);

        // generate "implements" sub-element
        implementsXML(classDoc);

        // generate "field" sub-elements
        FieldDoc[] fieldArray = classDoc.fields();
        for (int i = 0; i < fieldArray.length; ++i) {
            fieldXML(fieldArray[i]);
        }

        // generate "constructor" sub-elements
        ConstructorDoc[] constructorArray = classDoc.constructors();
        for (int i = 0; i < constructorArray.length; ++i) {
            constructorXML(constructorArray[i]);
        }

        // generate "method" sub-elements
        MethodDoc[] methodArray = classDoc.methods();
        for (int i = 0; i < methodArray.length; ++i) {
            methodXML(methodArray[i]);
        }

        // generate "innerclass" sub-elements
        ClassDoc[] innerClassArray = classDoc.innerClasses();
        for (int i = 0; i < innerClassArray.length; ++i) {
            innerClassXML(innerClassArray[i]);
        }

        handler.endElement(xmlns, localName, "class");
    }

    /**
     * Generates doc for element "extends_class"
     * <xmp><!ELEMENT extends_class (classref+)></xmp>
     */
    private void extendsXML(ClassDoc classDoc) throws SAXException {
        if (classDoc.superclass() != null) {
            handler.startElement(xmlns, localName, "extends_class", emptyAtts);
            createRefXML("classref", classDoc.superclass().qualifiedName());
            handler.endElement(xmlns, localName, "extends_class");
        }
    }

    /**
     * Generates doc for element "innerclass"
     * <xmp> <!ELEMENT innerclass (doc?,
     * extends?,
     * implements?,
     * field*,
     * constructor*,
     * method*)>
     * <!ATTLIST innerclass
     * %name;
     * %access;
     * %abstract;
     * %anonymous;
     * %final;
     * %static;></xmp>
     */
    private void innerClassXML(ClassDoc classDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", classDoc.name());
        String access = "package";
        if (classDoc.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);
        atts.addAttribute(xmlns, localName, "abstract", "String", "" + classDoc.isAbstract());
        String anonymous = "false";
        if (classDoc.name().equals("")) {
            anonymous = "true";
        }
        atts.addAttribute(xmlns, localName, "anonymous", "String", "" + anonymous);
        atts.addAttribute(xmlns, localName, "final", "String", "" + "" + classDoc.isFinal());
        atts.addAttribute(xmlns, localName, "static", "String", "" + "" + classDoc.isStatic());
        handler.startElement(xmlns, localName, "innerclass", atts);

        // generate "doc" sub-element
        docXML(classDoc);

        // generate "extends" sub-element
        extendsXML(classDoc);

        // generate "implements" sub-element
        implementsXML(classDoc);

        // generate "field" sub-elements
        FieldDoc[] fieldArray = classDoc.fields();
        for (int i = 0; i < fieldArray.length; ++i) {
            fieldXML(fieldArray[i]);
        }

        // generate "constructor" sub-elements
        ConstructorDoc[] constructorArray = classDoc.constructors();
        for (int i = 0; i < constructorArray.length; ++i) {
            constructorXML(constructorArray[i]);
        }

        // generate "method" sub-elements
        MethodDoc[] methodArray = classDoc.methods();
        for (int i = 0; i < methodArray.length; ++i) {
            methodXML(methodArray[i]);
        }

        handler.endElement(xmlns, localName, "innerclass");
    }

    /**
     * Generates doc for element "interface"
     * <xmp><!ELEMENT interface (doc?,
     * extends_interface?,
     * field*,
     * method*)>
     * <!ATTLIST interface
     * %name;
     * %access;></xmp>
     */
    private void interfaceXML(ClassDoc interfaceDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", interfaceDoc.name());
        String access = "package";
        if (interfaceDoc.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);
        handler.startElement(xmlns, localName, "interface", atts);

        // generate "doc" sub-element
        docXML(interfaceDoc);

        // generate "extends_interface"
        extends_interfaceXML(interfaceDoc);

        // generate "field" sub-elements
        FieldDoc[] fieldArray = interfaceDoc.fields();
        for (int i = 0; i < fieldArray.length; ++i) {
            fieldXML(fieldArray[i]);
        }

        // generate "method" sub-elements
        MethodDoc[] methodArray = interfaceDoc.methods();
        for (int i = 0; i < methodArray.length; ++i) {
            methodXML(methodArray[i]);
        }

        handler.endElement(xmlns, localName, "interface");
    }

    /**
     * Generates doc for element "extends_interface"
     * <xmp><!ELEMENT extends_interface (interfaceref+)></xmp>
     */
    private void extends_interfaceXML(ClassDoc interfaceDoc) throws SAXException {
        ClassDoc[] interfaceArray = interfaceDoc.interfaces();
        if (interfaceArray.length > 0) {
            handler.startElement(xmlns, localName, "extends_interface", emptyAtts);
            for (int i = 0; i < interfaceArray.length; ++i) {
                createRefXML("interfaceref", interfaceArray[i].qualifiedName());
            }
            handler.endElement(xmlns, localName, "extends_interface");
        }
    }

    /**
     * Generates doc for element "implements"
     * <xmp><!ELEMENT implements (interfaceref+)></xmp>
     */
    private void implementsXML(ClassDoc classDoc) throws SAXException {
        ClassDoc[] interfaceArray = classDoc.interfaces();
        if (interfaceArray.length > 0) {
            handler.startElement(xmlns, localName, "implements", emptyAtts);
            for (int i = 0; i < interfaceArray.length; ++i) {
                createRefXML("interfaceref", interfaceArray[i].qualifiedName());
            }
            handler.endElement(xmlns, localName, "implements");
        }
    }

    /**
     * Generates doc for element "throws"
     * <xmp><!ELEMENT throws (classref)+></xmp>
     */
    private void throwsXML(ExecutableMemberDoc member) throws SAXException {
        ThrowsTag[] tagArray = member.throwsTags();
        if (tagArray.length > 0) {
            handler.startElement(xmlns, localName, "throws", emptyAtts);
            for (int i = 0; i < tagArray.length; ++i) {
                ClassDoc exceptionClass = tagArray[i].exception();
                String name = null;
                if (exceptionClass == null) {
                    name = tagArray[i].exceptionName();
                } else {
                    name = tagArray[i].exception().qualifiedName();
                }
                createRefXML("classref", name);
            }
            handler.endElement(xmlns, localName, "throws");
        }
    }

    /**
     * Generates doc for following elements
     * <xmp> <!ELEMENT classref EMPTY>
     * <!ATTLIST classref %name;>
     * <!ELEMENT interfaceref EMPTY>
     * <!ATTLIST interfaceref %name;>
     * <!ELEMENT methodref EMPTY>
     * <!ATTLIST methodref %name;>
     * <!ELEMENT packageref EMPTY>
     * <!ATTLIST packageref %name;></xmp>
     */
    private void createRefXML(String elementName, String nameValue) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", nameValue);
        handler.startElement(xmlns, localName, elementName, atts);
        handler.endElement(xmlns, localName, elementName);
    }

    /**
     * Generates doc for "(classref|interfaceref|primitive)" sub-element
     */
    private void createTypeRef(Type type) throws SAXException {
        String qualifiedName = type.qualifiedTypeName();
        ClassDoc fieldType = type.asClassDoc();
        if (fieldType == null) {
            // primitive data type
            AttributesImpl subElmAtts = new AttributesImpl();
            subElmAtts.addAttribute(xmlns, localName, "type", "String", qualifiedName);
            handler.startElement(xmlns, localName, "primitive", subElmAtts);
            handler.endElement(xmlns, localName, "primitive");
        } else if (fieldType.isInterface()) {
            // interface
            createRefXML("interfaceref", qualifiedName);
        } else {
            // class
            createRefXML("classref", qualifiedName);
        }
    }

    /**
     * Generates doc for element "field"
     * <xmp> <!ELEMENT field (doc?, (classref | interfaceref | primitive))>
     * <!ATTLIST field
     * %name;
     * %access;
     * %dimension;
     * %synthetic;
     * %static;
     * %final;
     * %transient;
     * %volatile;></xmp>
     */
    private void fieldXML(FieldDoc field) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", field.name());
        String access = "package";

        if (field.isPrivate()) {
            access = "private";
        } else if (field.isProtected()) {
            access = "protected";
        } else if (field.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);

        atts.addAttribute(xmlns, localName, "dimension", "String", field.type().dimension());
        atts.addAttribute(xmlns, localName, "synthetic", "String", "" + field.isSynthetic());
        atts.addAttribute(xmlns, localName, "static", "String", "" + field.isStatic());
        atts.addAttribute(xmlns, localName, "final", "String", "" + field.isFinal());
        atts.addAttribute(xmlns, localName, "transient", "String", "" + field.isTransient());
        atts.addAttribute(xmlns, localName, "volatile", "String", "" + field.isVolatile());
        handler.startElement(xmlns, localName, "field", atts);

        // generate "doc" sub-element
        docXML(field);

        // generate "(classref|interfaceref|primitive)" sub-element
        createTypeRef(field.type()); // foo , field.qualifiedName());

        handler.endElement(xmlns, localName, "field");
    }

    /**
     * Generates doc for element "constructor"
     * <xmp><!ELEMENT constructor (doc?, parameter*, throws*)>
     * <!ATTLIST constructor
     * %name;
     * %access;
     * %synthetic;></xmp>
     */
    private void constructorXML(ConstructorDoc constrDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", constrDoc.qualifiedName());
        String access = "package";
        if (constrDoc.isPrivate()) {
            access = "private";
        } else if (constrDoc.isProtected()) {
            access = "protected";
        } else if (constrDoc.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);
        atts.addAttribute(xmlns, localName, "synthetic", "String", "" + constrDoc.isSynthetic());
        handler.startElement(xmlns, localName, "constructor", atts);

        // generate "doc" sub-element
        docXML(constrDoc);

        // generate "parameter" sub-elements
        Parameter[] parameterArray = constrDoc.parameters();
        for (int i = 0; i < parameterArray.length; ++i) {
            parameterXML(parameterArray[i]);
        }

        // generate "throws" sub-element
        throwsXML(constrDoc);

        handler.endElement(xmlns, localName, "constructor");
    }

    /**
     * Generates doc for element "method"
     * <xmp> <!ELEMENT method (doc?, returns, parameter*, throws*)>
     * <!ATTLIST method
     * %name;
     * %access;
     * %extensibility;
     * %native;
     * %synthetic;
     * %static;
     * %synchronized;></xmp>
     */
    private void methodXML(MethodDoc methodDoc) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        //atts.addAttribute(xmlns, localName, "", String, );
        atts.addAttribute(xmlns, localName, "name", "String", methodDoc.name());

        String access = "package";
        if (methodDoc.isPrivate()) {
            access = "private";
        } else if (methodDoc.isProtected()) {
            access = "protected";
        } else if (methodDoc.isPublic()) {
            access = "public";
        }
        atts.addAttribute(xmlns, localName, "access", "String", access);

        String extensibility = "default";
        if (methodDoc.isAbstract()) {
            extensibility = "abstract";
        } else if (methodDoc.isFinal()) {
            extensibility = "final";
        }
        atts.addAttribute(xmlns, localName, "extensiblity", "String", extensibility);

        atts.addAttribute(xmlns, localName, "native", "String", "" + methodDoc.isNative());
        atts.addAttribute(xmlns, localName, "synthetic", "String", "" + methodDoc.isSynthetic());
        atts.addAttribute(xmlns, localName, "static", "String", "" + methodDoc.isStatic());
        atts.addAttribute(xmlns, localName, "synchronized", "String", "" + methodDoc.isSynchronized());
        handler.startElement(xmlns, localName, "method", atts);

        // generate "doc" sub-element
        docXML(methodDoc);

        // generate "returns" sub-element
        returnsXML(methodDoc.returnType());

        // generate "parameter" sub-elements
        Parameter[] parameterArray = methodDoc.parameters();
        for (int i = 0; i < parameterArray.length; ++i) {
            parameterXML(parameterArray[i]);
        }

        // generate "throws" sub-element
        throwsXML(methodDoc);

        handler.endElement(xmlns, localName, "method");
    }

    /**
     * Generates doc for element "returns"
     * <xmp> <!ELEMENT returns (classref | interfaceref | primitive)>
     * <!ATTLIST returns %dimension;></xmp>
     */
    private void returnsXML(Type type) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "dimension", "String", type.dimension());
        handler.startElement(xmlns, localName, "returns", atts);

        // generate "(classref|interfaceref|primitive)" sub-element
        createTypeRef(type);

        handler.endElement(xmlns, localName, "returns");
    }

    /**
     * Generates doc for element "parameter"
     * <xmp> <!ELEMENT parameter (classref | interfaceref | primitive)>
     * <!ATTLIST parameter
     * %name;
     * %final;
     * %dimension;></xmp>
     */
    private void parameterXML(Parameter parameter) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", parameter.name());
        boolean isFinal = false;
        Type type = parameter.type();
        if (type.asClassDoc() == null) {
            isFinal = true;
        }
        atts.addAttribute(xmlns, localName, "final", "String", "" + "" + isFinal);
        atts.addAttribute(xmlns, localName, "dimension", "String", parameter.type().dimension());
        handler.startElement(xmlns, localName, "parameter", atts);

        // generate "(classref|interfaceref|primitive)" sub-element
        createTypeRef(parameter.type());

        handler.endElement(xmlns, localName, "parameter");
    }

    /**
     * Generates doc for element "doc"
     * <xmp><!ELEMENT doc (#PCDATA |
     * linktag |
     * authortag |
     * versiontag |
     * paramtag |
     * returntag |
     * exceptiontag |
     * throwstag |
     * seetag |
     * sincetag |
     * deprecatedtag |
     * serialtag |
     * serialfieldtag |
     * serialdatatag)*></xmp>
     */
    private void docXML(Doc doc) throws SAXException {
        String commentText = "";
        boolean createDoc = false;
        commentText = doc.commentText();
        if (!commentText.equals("")) {
            createDoc = true;
        }
        Tag[] tags = doc.tags();
        if (tags.length > 0) {
            createDoc = true;
        }
        if (createDoc) {
            handler.startElement(xmlns, localName, "doc", emptyAtts);
            if (!commentText.equals("")) {
                handler.characters(commentText.toCharArray(), 0, commentText.length());
            }
            for (int i = 0; i < tags.length; ++i) {
                tagXML(tags[i]);
            }
            handler.endElement(xmlns, localName, "doc");
        }
    }

    /**
     * Generates doc for all tag elements.
     */
    private void tagXML(Tag tag) throws SAXException {
        String name = tag.name().substring(1) + "tag";
        if (!tag.text().equals("")) {
            handler.startElement(xmlns, localName, name, emptyAtts);
            handler.characters(tag.text().toCharArray(), 0, tag.text().length());
            handler.endElement(xmlns, localName, name);
        }
    }

    public static boolean start(RootDoc root) {
        try {
            new XMLDoclet(root);
            return true;
        } catch (Exception e) {
            log.error( "Exception: " + e.getMessage(), e );
            return false;
        }
    }
}
