package pl.trzepacz.population.domain;

import java.util.List;

public interface PopulationRepository {
    List<Population> findPopulations();
}
