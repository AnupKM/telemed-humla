package com.telemed.backend.util;

import com.telemed.backend.dto.NameParts;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NameUtil {

    public static NameParts parseFullname(String fullname) {
        NameParts nameParts = new NameParts();
        if (fullname != null && !fullname.trim().isEmpty()) {
            String[] parts = fullname.trim().split("\\s+");
            if (parts.length == 1) {
                nameParts.setFirstName(parts[0]);
            } else if (parts.length == 2) {
                nameParts.setFirstName(parts[0]);
                nameParts.setLastName(parts[1]);
            } else {
                nameParts.setFirstName(parts[0]);
                nameParts.setLastName(parts[parts.length - 1]);

                StringBuilder middle = new StringBuilder();
                for (int i = 1; i < parts.length - 1; i++) {
                    middle.append(parts[i]);
                    if (i < parts.length - 2) {
                        middle.append(" ");
                    }
                }
                nameParts.setMiddleName(middle.toString());
            }
        }
        return nameParts;
    }
}
