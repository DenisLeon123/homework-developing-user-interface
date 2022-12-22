package com.sample.airtickets.screen.ticketreservation;

import com.sample.airtickets.app.TicketService;
import com.sample.airtickets.entity.Airport;
import com.sample.airtickets.entity.Flight;
import com.sample.airtickets.screen.ticket.TicketBrowse;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.component.*;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.executor.BackgroundTask;
import io.jmix.ui.executor.BackgroundTaskHandler;
import io.jmix.ui.executor.BackgroundWorker;
import io.jmix.ui.executor.TaskLifeCycle;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.jmix.ui.app.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

@UiController("TicketReservation")
@UiDescriptor("ticket-reservation.xml")
public class TicketReservation extends Screen {

    @Autowired
    TicketService ticketService;

    @Autowired
    private Notifications notifications;
    @Autowired
    private CollectionContainer<Flight> flightsDc;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private EntityComboBox<Airport> fromSelector;
    @Autowired
    private EntityComboBox<Airport> toSelector;
    @Autowired
    private DateField<LocalDate> departureDateSelector;
    @Autowired
    private ProgressBar progressBarSearch;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private InputDialogFacet reserveTicket;

    @Subscribe("filterSearch")
    public void onFilterSearch(Action.ActionPerformedEvent event) {

        if (fromSelector.getValue() == null
                && toSelector.getValue() == null
                && departureDateSelector.getValue() == null) {
            notifications.create()
                    .withCaption(messageBundle.getMessage("validation.fieldsNotFilled.message"))
                    .withType(Notifications.NotificationType.WARNING)
                    .show();

            fromSelector.focus();
            return;
        }

        progressBarSearch.setVisible(true);

        BackgroundTask<Integer, List<Flight>> task = new BackgroundTask<Integer, List<Flight>>(100) {
            @Override
            public List<Flight> run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                return ticketService.searchFlights(fromSelector.getValue(), toSelector.getValue(), departureDateSelector.getValue());
            }

            @Override
            public void done(List<Flight> result) {
                flightsDc.setItems(result);
                progressBarSearch.setVisible(false);
            }
        };

        BackgroundTaskHandler taskHandler = backgroundWorker.handle(task);
        taskHandler.execute();
    }

    @Install(to = "flightsTable.action", subject = "columnGenerator")
    private Component flightsTableActionColumnGenerator(Flight flight) {
        LinkButton button = uiComponents.create(LinkButton.class);
        button.setId("actionReserve");
        button.setCaption("Reserve");
        button.addClickListener((event) -> {
            reserveTicket.create().show();
        });
        return button;
    }

    @Install(to = "reserveTicket", subject = "dialogResultHandler")
    private void reserveTicketDialogResultHandler(InputDialog.InputDialogResult inputDialogResult) {

        if (inputDialogResult.getCloseAction() == INPUT_DIALOG_OK_ACTION) {
            Map<String, Object> values = inputDialogResult.getValues();
            Flight flight = flightsDc.getItem();

            ticketService.saveTicket((String) values.get("passengerName"),
                    (String) values.get("passportNumber"),
                    (String) values.get("telephone"),
                    flight);

            screenBuilders
                    .screen(this)
                    .withScreenClass(TicketBrowse.class)
                    .withOpenMode(OpenMode.NEW_TAB)
                    .build()
                    .show();

        }
    }
}