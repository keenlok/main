package seedu.thanepark.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.thanepark.commons.core.LogsCenter;
import seedu.thanepark.commons.events.ui.JumpToListRequestEvent;
import seedu.thanepark.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.thanepark.model.ride.Ride;
import seedu.thanepark.ui.exceptions.AccessibilityException;

/**
 * Panel containing the list of persons.
 */
public class RideListPanel extends UiPart<Region> {
    private static final String FXML = "RideListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(RideListPanel.class);

    @FXML
    private ListView<Ride> personListView;

    public RideListPanel(ObservableList<Ride> rideList) {
        super(FXML);
        setConnections(rideList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<Ride> rideList) {
        personListView.setItems(rideList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        personListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in ride list panel changed to : '" + newValue + "'");
                        raise(new PersonPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code RideCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            personListView.scrollTo(index);
            personListView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Ride} using a {@code RideCard}.
     */
    class PersonListViewCell extends ListCell<Ride> {
        @Override
        protected void updateItem(Ride ride, boolean empty) {
            super.updateItem(ride, empty);

            if (empty || ride == null) {
                setGraphic(null);
                setText(null);
            } else {
                try {
                    setGraphic(new RideCard(ride, getIndex() + 1).getRoot());
                } catch (AccessibilityException ae) {
                    logger.warning(ae.getMessage());
                }
            }
        }
    }

}