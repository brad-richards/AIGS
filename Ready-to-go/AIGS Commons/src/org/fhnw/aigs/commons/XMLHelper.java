package org.fhnw.aigs.commons;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.fhnw.aigs.commons.communication.Message;
import java.util.logging.Level;
import javax.xml.bind.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * This class provides XML-centred helper methods.<br>
 * v1.0 Initial release<br>
 * v1.1 Added some further error handling<br>
 * v1.2 Changing of logging
 *
 * @author Matthias Stöckli (v1.0)
 * @version v1.2 (Raphael Stoeckli, 26.02.2015)
 */
public class XMLHelper {

    /**
     * This method returns a {@link javax.xml.bind.Marshaller} . This object
     * belongs to {@link JAXBContext} which is responsible for unmarshalling XML
     * plain text into objects. <br>
     * This method modifies the mechanism of JAXB slightly so that it produces
     * unformatted XML output. In consequence, the XML will contain no line
     * breaks. This is useful when sending it to the server or the clients as
     * the client or the server can just read the first line from the buffer and
     * immediately interpret it as XML.
     *
     * @param message The message to be unformatted.
     * @return The resulting Marshaller
     */
    public static Marshaller getUnformattedXMLMarshaller(Message message) {
        // HACK: The very first call of a running game can cause a crash in marshaller. All further attemps running without problems. Problem with ClassLoader???
        JAXBContext context;
        Marshaller marshaller = null;
        String errorMessage = "";
        Exception outputException = null;
       
       boolean processingError = false;
       for(int i = 0; i < 5; i++)  // In case of an exception, let's try n times and then give up (and handle error message)
       { 
            try {
                context = JAXBContext.newInstance(message.getClass());
                marshaller = context.createMarshaller();

                // Set the JAXB_FORMATTED_OUTPUT property.
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
                return marshaller;

            } catch (JAXBException ex) {
                //Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
                errorMessage = "";
                outputException = ex;
                processingError = true;
            }
            catch (Exception ex) // All other exceptions
            {
                errorMessage = "An unknown error occured.";
                outputException = ex;
                processingError = true;
               // Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
            }
       }
       if (processingError == true)
       {         
          // Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, errorMessage, outputException);
           LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, errorMessage, outputException);
       }
        return marshaller;

    }

    /**
     * Pretty prints/formats XML. Inspired by "zerix". See
     * http://www.tutorials.de/java/276286-xmlstring-xml-format-ausgeben-lassen.html
     * @return returns formated XML string
     * @param uglyXml Unformatted XML input.
     */
    public static String prettyPrintXml(String uglyXml) {
        try {
            StringWriter writer = new StringWriter();

            // Use Transformer to format the string
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "1");
            transformer.transform(new StreamSource(new StringReader(uglyXml)),
                    new StreamResult(writer));
            StringBuffer buffer = writer.getBuffer();
            return buffer.toString();
        } catch (TransformerConfigurationException ex) {
            //Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
            LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            //Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
            LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, null, ex);
        }
        catch (Exception ex) // All other exceptions
        {
            //Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
            LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, "An unknown error occured.", ex);
        }
        return "";
    }

    /**
     * Use this main method if you want to generate XSDs out of the message
     * classes.
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        generateXSDs();
    }

    /**
     * Creates XSDs using the generateSchema method of JAXBContext. Based on
     * http://stackoverflow.com/questions/7212064/is-it-possible-to-generate-a-xsd-from-a-jaxb-annotated-class
     */
    private static void generateXSDs() throws IOException, ClassNotFoundException {

        // List of all message classes
        String[] messageClassNames = new String[]{"BadInputMessage", "ClientClosedMessage",
            "ExceptionMessage", "FieldChangedMessage", "FieldClickFeedbackMessage",
            "FieldClickMessage", "GameEndsMessage", "GameStartMessage",
            "IdentificationMessage", "IdentificationResponseMessage",
            "JoinMessage", "KeepAliveMessage", "PlayerChangedMessage",
            "ResultMessage", "Message"};

        JAXBContext jaxbContext;

        try {
            Class messageClazz = null;
            // Load all the classes and create a JAXBContext out of it
            // then generate a schema using a SchemaOutputResolver.
            for (String messageClassName : messageClassNames) {
                messageClazz = Class.forName("org.fhnw.aigs.commons.communication."
                        + messageClassName);
                jaxbContext = JAXBContext.newInstance(messageClazz);
                SchemaOutputResolver sor = new XMLHelper.XSDOutputResolver(messageClassName);
                jaxbContext.generateSchema(sor);
            }
        } catch (JAXBException ex) {
            //Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
            LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, null, ex);
        }
        catch (IOException | ClassNotFoundException ex) // All other exceptions
        {
           // Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, "An unknown error occured.", ex);
            LogRouter.log(XMLHelper.class.getName(), Level.SEVERE, "An unknown error occured.", ex);
        }

    }

    /**
     * Generates XSDs, use it with JAXB.
     */
    private static class XSDOutputResolver extends SchemaOutputResolver {
        // File name of XSD file

        String fileName;

        private XSDOutputResolver(String messageClassName) {
            this.fileName = messageClassName;
        }

        /**
         * Save the schema in the folder "Schemata".
         *
         * @param namespaceUri The namespace.
         * @param suggestedFileName Automatically generated name.
         * @return The result (file)
         * @throws IOException
         */
        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
            File file = new File("..\\Schemata\\" + fileName + ".xsd");
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }
    }
}
