package ru.javaops.masterjava.xml.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.*;


/**
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {

    protected JaxbMarshaller jaxbMarshaller;
    protected JaxbUnmarshaller jaxbUnmarshaller;
    protected Schema schema;
    private final ThreadLocal<JaxbMarshaller> marshallerThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<JaxbUnmarshaller> unmarshallerThreadLocal = new ThreadLocal<>();
    private final JAXBContext ctx;

    public JaxbParser(Class... classesToBeBound) {
        try {
            ctx = JAXBContext.newInstance(classesToBeBound);
            init(ctx);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //    http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JaxbParser(String context) {
        try {
            ctx = JAXBContext.newInstance(context);
            init(ctx);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void init(JAXBContext ctx) throws JAXBException {
        jaxbMarshaller = getJaxbMarshaller(ctx);
        jaxbUnmarshaller = getJaxbUnmarshaller(ctx);
    }

    // Unmarshaller
    public <T> T unmarshal(InputStream is) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(is);
    }

    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(reader);
    }

    public <T> T unmarshal(String str) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(str);
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return jaxbUnmarshaller.unmarshal(reader, elementClass);
    }

    // Marshaller
    public void setMarshallerProperty(String prop, Object value) {
        try {
            jaxbMarshaller.setProperty(prop, value);
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String marshal(Object instance) throws JAXBException {
        return jaxbMarshaller.marshal(instance);
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        jaxbMarshaller.marshal(instance, writer);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        jaxbUnmarshaller.setSchema(schema);
        jaxbMarshaller.setSchema(schema);
    }

    public void validate(String str) throws IOException, SAXException {
        validate(new StringReader(str));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }

    public JaxbMarshaller getJaxbMarshaller(JAXBContext ctx) throws JAXBException {
        JaxbMarshaller jaxbMarshaller = marshallerThreadLocal.get();
        if (jaxbMarshaller == null) {
            jaxbMarshaller = new JaxbMarshaller(ctx);
            marshallerThreadLocal.set(jaxbMarshaller);
        }
        return jaxbMarshaller;
    }

    public JaxbUnmarshaller getJaxbUnmarshaller(JAXBContext ctx) throws JAXBException {
        JaxbUnmarshaller jaxbUnmarshaller = unmarshallerThreadLocal.get();
        if (jaxbUnmarshaller == null) {
            jaxbUnmarshaller = new JaxbUnmarshaller(ctx);
            unmarshallerThreadLocal.set(jaxbUnmarshaller);
        }
        return jaxbUnmarshaller;
    }
}
