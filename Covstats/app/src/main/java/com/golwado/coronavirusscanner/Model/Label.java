package com.golwado.coronavirusscanner.Model;

import java.util.HashMap;
import java.util.Map;

public class Label {
    private final String apiName;
    private final Map<Language , String> labelContextByLanguageCode;

    protected Label(String apiName){
        this.apiName = apiName;
        labelContextByLanguageCode = new HashMap<>();
    }

    protected void addTranslation(Language language , String translation){
        labelContextByLanguageCode.put(language , translation);
    }

    protected String getLabel(Language language){
        return labelContextByLanguageCode.get(language);
    }
}
