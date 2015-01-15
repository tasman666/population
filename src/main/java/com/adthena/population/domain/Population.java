package com.adthena.population.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Population {

    private final Integer year;
    private final Long number;

    public Population(Integer year, Long number) {
        Objects.requireNonNull(year);
        Objects.requireNonNull(number);
        this.year = year;
        this.number = number;
    }

    public int getYear() {
        return year;
    }

    public long getNumber() {
        return number;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
