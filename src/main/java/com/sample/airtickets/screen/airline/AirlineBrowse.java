package com.sample.airtickets.screen.airline;

import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Airline;
import liquibase.util.StringUtil;

@UiController("Airline.browse")
@UiDescriptor("airline-browse.xml")
@LookupComponent("table")
public class AirlineBrowse extends MasterDetailScreen<Airline> {
    @Install(to = "iataCodeField", subject = "formatter")
    private String iataCodeFieldFormatter(String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Install(to = "table.iataCode", subject = "formatter")
    private String tableIataCodeFormatter(Object object) {
        String value = (String) object;
        return value != null ? value.toUpperCase() : null;
    }
}