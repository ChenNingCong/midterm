package org.example.type;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface Actor {
    void action();

    default String getFormatCurrentDate() {
        return new SimpleDateFormat("MM/dd/yyyy").format(new Date());
    }
}
