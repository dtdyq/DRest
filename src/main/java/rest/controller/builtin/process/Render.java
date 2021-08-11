
package rest.controller.builtin.process;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import rest.controller.builtin.model.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * post process for response: beautify for response body .etc
 */
public class Render implements Processer<Response, Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Render.class);

    private static String formatXml(String unformattedXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(unformattedXml));
            final Document document = db.parse(is);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOGGER.error("format xml data error ", e);
            return unformattedXml;
        }
    }

    @Override
    public Void process(Response data) {
        LOGGER.info("start render process:{}", data);
        if (data.header().keySet().stream().anyMatch(s -> s.toLowerCase().equals("content-type"))) {
            String ctType = data.header()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().equals("content-type"))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("");
            if (ctType.contains("application/json")) {
                data.body(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(data.body())));
            }
            if (ctType.contains("application/xml") || ctType.contains("text/xml")) {
                data.body(formatXml(data.body()));
            }
        }
        return null;
    }
}
