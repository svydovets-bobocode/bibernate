package com.bobocode.svydovets.bibernate.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlConfiguration implements ConfigurationSource {
    private final Map<String, String> properties = new HashMap<>();

    public XmlConfiguration(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NodeList propertyNodes = document.getElementsByTagName("property");

            IntStream.range(0, propertyNodes.getLength())
                    .mapToObj(propertyNodes::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(Element.class::cast)
                    .forEach(
                            element -> {
                                String name = element.getAttribute("name");
                                String value = element.getTextContent();
                                properties.put(name, value);
                            });
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Failed to load configuration properties from XML file", e);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}
