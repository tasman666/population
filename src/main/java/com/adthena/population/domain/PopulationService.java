package com.adthena.population.domain;

import java.util.Comparator;
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
        return findTopPopulationDifferences(topNumber,
                                            populationDifference -> populationDifference.isPopulationGrowth(),
                                            comparing(PopulationDifference::getValue).reversed());
    }

    public List<PopulationDifference> findTopPopulationDeclines(int topNumber) {
        return findTopPopulationDifferences(topNumber,
                                            populationDifference -> populationDifference.isPopulationDecline(),
                                            comparing(PopulationDifference::getValue));
    }

    private List<PopulationDifference> findTopPopulationDifferences(int topNumber,
                                                                    Predicate<PopulationDifference> populationDifferencePredicate,
                                                                    Comparator<PopulationDifference> populationDifferenceComparator) {
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
                                .filter(populationDifferencePredicate)
                )
                .sorted(populationDifferenceComparator)
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
