package seedu.address.commons.util;

import static j2html.TagCreator.div;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.img;
import static j2html.TagCreator.p;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import j2html.tags.ContainerTag;
import seedu.address.model.ride.Ride;

/**
 * Singleton Class for generating a Html page used to display ride details.
 */
public class RidePageGenerator extends HtmlGenerator<Ride> {
    private static RidePageGenerator instance;

    /**
     * Required for singleton.
     */
    private RidePageGenerator() {
        ;
    }

    public static RidePageGenerator getInstance() {
        if (instance == null) {
            instance = new RidePageGenerator();
        }
        return instance;
    }

    /**
     * Generates a Html report with commandEntryList as entries as fileName.
     */
    public void generateHtml(String title, Ride data, FilePathToUrl filePathToUrl)
        throws IOException {
        List<Ride> dataAsList = new LinkedList<>();
        dataAsList.add(data);
        generateHtml(title, dataAsList, filePathToUrl);
    }

    @Override
    public ContainerTag generateHeader(String title) {
        return div(
            div(
                p(h2().withId("name"))
            ).withStyle("height: auto; min-width: 200px; width: 50%; display: inline-block; left: 0px;"
                + "word-wrap: break-word"),
            div(
                img().attr("src=\"images/ride.png\" alt=\"ride\"").withStyle("height: auto; width: 100%")
            ).withStyle("min-width: 200px; height: auto; width: 40%; display: inline-block; right: 0px")
        );
    }

    @Override
    public ContainerTag generateBody(List<Ride> data) {
        return generateVerticalTable(data.get(0));
    }

}