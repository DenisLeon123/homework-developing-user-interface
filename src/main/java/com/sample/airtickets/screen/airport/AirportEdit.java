package com.sample.airtickets.screen.airport;

import io.jmix.ui.component.HasValue;
import io.jmix.ui.component.TextInputField;
import io.jmix.ui.screen.*;
import com.sample.airtickets.entity.Airport;

@UiController("Airport.edit")
@UiDescriptor("airport-edit.xml")
@EditedEntityContainer("airportDc")
public class AirportEdit extends StandardEditor<Airport> {

    @Subscribe("codeField")
    public void onCodeFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        String text = event.getValue();
        if (text != null) {
            event.getSource().setValue(event.getValue().toUpperCase());
        }
    }
}