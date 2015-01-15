package com.adthena.population.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PopulationService {

    private PopulationRepository populationRepository;

    public PopulationService(PopulationRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    public List<PopulationDifference> findTopPopulationGrowths(int topNumber) {
        if (topNumber < 1) {
            throw new IllegalArgumentException("Top number below 1");
        }
        List<PopulationDifference> populationDifferences = new ArrayList<>();
        Population previousPopulation = null;
        for (Population population : populationRepository.findPopulationsSortedByYear()) {
            if (previousPopulation != null && previousPopulation.getYear() == population.getYear() - 1) {
                long difference = population.getNumber() - previousPopulation.getNumber();
                if (difference > 0) {
                    populationDifferences.add(new PopulationDifference(population.getYear(), difference));
                }
            }
            previousPopulation = population;
        }
        return populationDifferences
                .stream()
                .sorted((pd1, pd2) -> pd2.getValue().compareTo(pd1.getValue()))
                .limit(topNumber)
                .collect(Collectors.toList());
    }
}
