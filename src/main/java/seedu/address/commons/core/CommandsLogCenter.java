package seedu.address.commons.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import seedu.address.model.logging.CommandEntry;
import seedu.address.storage.XmlAdaptedCommandEntry;
import seedu.address.storage.XmlListOfCommandEntry;

/**
 * Logs down command history into a xml file. Stops writing when file size exceeds 5MB.
 */
public class CommandsLogCenter {
    private static final String STANDARDIZED_ENCODING = "UTF-8";
    private static final String STANDARDIZED_XML_HEADER = String.format("<?xml version=\"1.0\" encoding=\"%1$s\"?>",
        STANDARDIZED_ENCODING);
    private static final String LIST_HEADER = "<xmlListOfCommandEntry>\n";
    private static final String LIST_ENDING = "\n</xmlListOfCommandEntry>\n";
    private static final String MESSAGE_FILE_SIZE_EXCEEDED = "Maximum file size exceeded, %1$s";
    public static final String MESSAGE_LOG_INACCESSIBLE = "%1$s cannot be accessed";
    private static final int MAX_FILE_SIZE_IN_BYTES = (int) (Math.pow(2, 20) * 5); // 5MB
    private static String logFile = "commandHistory.xml";

    private static File file;
    private static boolean isLogAccessible = true;

    /**
     * Initializes, create the command log file with header if it does not exist.
     */
    public static void init() {
        file = new File(logFile);
        try {
            if (!file.exists()) {
                isLogAccessible = isLogAccessible && file.createNewFile();
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.append(STANDARDIZED_XML_HEADER + "\n");
                fileWriter.append(LIST_HEADER);
                fileWriter.close();
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            isLogAccessible = file.exists() && file.canWrite();
        }
    }

    /**
     * Used for testing only
     */
    public static void init(String fileName) {
        logFile = fileName;
        file = new File(logFile);
        delete();
        init();
        file.deleteOnExit();
    }

    /**
     * Writes the given xmlCommandEntryString to file. Throws IOException if file does not exist or cannot be written.
     */
    public static void log(CommandEntry commandEntry) throws IOException, JAXBException {
        if (!isLogAccessible) {
            throw new IOException(String.format(MESSAGE_LOG_INACCESSIBLE, logFile));
        }
        if (file.length() > MAX_FILE_SIZE_IN_BYTES) {
            throw new IOException(String.format(MESSAGE_FILE_SIZE_EXCEEDED, String.valueOf(MAX_FILE_SIZE_IN_BYTES)));
        }
        XmlAdaptedCommandEntry xmlCommandEntry = new XmlAdaptedCommandEntry(commandEntry);
        JAXBContext context = JAXBContext.newInstance(xmlCommandEntry.getClass());
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        standardizeXmlOutput(marshaller);
        marshaller.marshal(xmlCommandEntry, stringWriter);

        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.append(stringWriter.toString() + "\n");
        fileWriter.close();
    }

    /**
     * Returns a XmlListOfCommandEntry parsed from the command history log file. If file size exceeds twice of size
     * limit, IOException will be thrown.
     */
    public static XmlListOfCommandEntry retrieve() throws IOException, JAXBException {
        if (!isLogAccessible) {
            throw new IOException(String.format(MESSAGE_LOG_INACCESSIBLE, logFile));
        }
        if (file.length() > 2 * MAX_FILE_SIZE_IN_BYTES) {
            throw new IOException(String.format(MESSAGE_FILE_SIZE_EXCEEDED,
                String.valueOf(2 * MAX_FILE_SIZE_IN_BYTES)));
        }
        byte[] encoded = Files.readAllBytes(Paths.get(logFile));
        String x = new String(encoded, STANDARDIZED_ENCODING) + LIST_ENDING;
        StringReader stringReader = new StringReader(x);

        JAXBContext jaxbContext = JAXBContext.newInstance(XmlListOfCommandEntry.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (XmlListOfCommandEntry) unmarshaller.unmarshal(stringReader);
    }

    /**
     * Deletes the log file for command history.
     */
    public static void delete() {
        file.delete();
    }

    /**
     * Sets the marshaller to use a standard XML output.
     */
    private static void standardizeXmlOutput(Marshaller marshaller) throws PropertyException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty("com.sun.xml.bind.xmlHeaders", CommandsLogCenter.STANDARDIZED_XML_HEADER);
    }

}
