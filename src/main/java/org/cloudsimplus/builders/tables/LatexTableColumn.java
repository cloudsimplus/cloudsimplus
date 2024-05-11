package org.cloudsimplus.builders.tables;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class LatexTableColumn {
    private final String name;
    private final String specifier;

    public LatexTableColumn(String name, String specifier) {
        this.name = name;
        this.specifier = specifier;
    }

    public String getName() {
        return name;
    }

    public String getSpecifier() {
        return specifier;
    }
}
