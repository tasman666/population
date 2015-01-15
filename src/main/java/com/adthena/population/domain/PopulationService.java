package com.adthena.population.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class PopulationService {

    private PopulationRepository populationRepository;

    public PopulationService(PopulationRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    public List<PopulationDifference> findTopPopulationGrowths(int topNumber) {
        if (topNumber < 1) {
            throw new IllegalArgumentException("Top number below 1");
        }
        List<Population> populations = populationRepository.findPopulationsSortedByYear();
        return populations
                .stream()
                .flatMap(population -> populations
                                .stream()
                                .filter(previousYearPopulationFor(population))
                                .map(previousPopulation -> createPopulationDifference(population, previousPopulation))
                                .filter(populationDifference -> populationDifference.isPopulationGrowth())
                )
                .sorted(comparing(PopulationDifference::getValue).reversed())
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    private Predicate<Population> previousYearPopulationFor(Population population) {
        return p -> p.getYear() == population.getYear() - 1;
    }

    private PopulationDifference createPopulationDifference(Population population, Population previousPopulation) {
        return new PopulationDifference(population.getYear(), population.getNumber() - previousPopulation.getNumber());
    }
}
