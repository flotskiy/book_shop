package com.github.flotskiy.FlotskiyBookShopApp.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class PanelizationSummary {
    @JsonProperty("containsEpubBubbles")
    public boolean getContainsEpubBubbles() {
        return this.containsEpubBubbles;
    }

    public void setContainsEpubBubbles(boolean containsEpubBubbles) {
        this.containsEpubBubbles = containsEpubBubbles;
    }

    boolean containsEpubBubbles;

    @JsonProperty("containsImageBubbles")
    public boolean getContainsImageBubbles() {
        return this.containsImageBubbles;
    }

    public void setContainsImageBubbles(boolean containsImageBubbles) {
        this.containsImageBubbles = containsImageBubbles;
    }

    boolean containsImageBubbles;
}
