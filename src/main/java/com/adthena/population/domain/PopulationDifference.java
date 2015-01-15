package com.adthena.population.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class PopulationDifference {

    private final Integer year;
    private final Long value;

    public PopulationDifference(Integer year, Long value) {
        Objects.requireNonNull(year);
        Objects.requireNonNull(value);
        this.year = year;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Long getValue() {
        return value;
    }

    public boolean isPopulationGrowth() {
        return value > 0;
    }

    public boolean isPopulationDecline() {
        return value < 0;
    }
}
