package com.teamtea.eclipticseasons.api.data.season.definition;

public interface ISeasonChangeContext {

    public static ISeasonChangeContext of() {
        return new Impl();
    }

    public class Impl implements ISeasonChangeContext {
    }
}
