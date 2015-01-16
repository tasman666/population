package com.adthena.population.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

public class PopulationService {

    private PopulationRepository populationRepository;

    public PopulationService(PopulationRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    public List<PopulationDifference> findTopPopulationGrowths(int topNumber) {
        return findTopPopulationDifferences(topNumber,
                                            PopulationDifference::isPopulationGrowth,
                                            comparing(PopulationDifference::getValue).reversed());
    }

    public List<PopulationDifference> findTopPopulationDeclines(int topNumber) {
        return findTopPopulationDifferences(topNumber,
                                            PopulationDifference::isPopulationDecline,
                                            comparing(PopulationDifference::getValue));
    }

    public Optional<Double> findLargestDeviationFromAverageGrowthDifference() {
        List<Population> populations = populationRepository.findPopulations();
        Predicate<PopulationDifference> allPopulationDifferences = populationDifference ->
                populationDifference.isPopulationDecline() || populationDifference.isPopulationGrowth();
        OptionalDouble averageGrowthDifference = calculateAverageGrowthDifference(populations, allPopulationDifferences);
        return createPopulationDifferencesStream(populations, allPopulationDifferences)
                .map(pd -> Math.abs(pd.getValue() - averageGrowthDifference.getAsDouble()))
                .sorted(reverseOrder())
                .findFirst();
    }

    private OptionalDouble calculateAverageGrowthDifference(List<Population> populations, Predicate<PopulationDifference> allPopulationDifferences) {
        return createPopulationDifferencesStream(populations, allPopulationDifferences)
                .mapToDouble(PopulationDifference::getValue)
                .average();
    }

    private List<PopulationDifference> findTopPopulationDifferences(int topNumber,
                                                                    Predicate<PopulationDifference> populationDifferencePredicate,
                                                                    Comparator<PopulationDifference> populationDifferenceComparator) {
        if (topNumber < 1) {
            throw new IllegalArgumentException("Top number below 1");
        }
        List<Population> populations = populationRepository.findPopulations();
        return createPopulationDifferencesStream(populations, populationDifferencePredicate)
                .sorted(populationDifferenceComparator)
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    private Stream<PopulationDifference> createPopulationDifferencesStream(List<Population> populations,
                                                                           Predicate<PopulationDifference> populationDifferencePredicate) {
        return populations
                .stream()
                .flatMap(population -> populations
                                .stream()
                                .filter(previousYearPopulationFor(population))
                                .map(previousPopulation -> createPopulationDifference(population, previousPopulation))
                                .filter(populationDifferencePredicate)
                );
    }

    private Predicate<Population> previousYearPopulationFor(Population population) {
        return p -> p.getYear() == population.getYear() - 1;
    }

    private PopulationDifference createPopulationDifference(Population population, Population previousPopulation) {
        return new PopulationDifference(population.getYear(), population.getNumber() - previousPopulation.getNumber());
    }

}
