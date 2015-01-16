package com.adthena.population.domain

import spock.lang.Specification
import spock.lang.Unroll

class PopulationServiceSpec extends Specification {

    private PopulationRepository populationRepository = Mock(PopulationRepository)
    private PopulationService populationService = new PopulationService(populationRepository)

    @Unroll
    def "should find top 2 population growths when populations #populations"() {
        given:
            populationRepository.findPopulations() >> populations
        when:
            List<PopulationDifference> populationGrowths = populationService.findTopPopulationGrowths(2)
        then:
            populationGrowths == expectedPopulationGrowths
        where:
             populations                                | expectedPopulationGrowths
                []                                      | []

                [new Population(2010, 35000)]           | []

                [new Population(2010, 35000),
                 new Population(2012, 45000)]           | []

                [new Population(2010, 35000),
                 new Population(2011, 35000)]           | []

                [new Population(2010, 35000),
                 new Population(2011, 25000),
                 new Population(2012, 15000),
                 new Population(2013, 15000)]           | []

                [new Population(2010, 35000),
                 new Population(2011, 45000),
                 new Population(2012, 85000),
                 new Population(2013, 90000)]           | [new PopulationDifference(2012, 40000), new PopulationDifference(2011, 10000)]

                [new Population(2010, 35000),
                 new Population(2011, 45000),
                 new Population(2012, 55000),
                 new Population(2013, 65000)]           | [new PopulationDifference(2011, 10000), new PopulationDifference(2012, 10000)]

                [new Population(2010, 35000),
                 new Population(2011, 25000),
                 new Population(2012, 55000)]           | [new PopulationDifference(2012, 30000)]


    }

    @Unroll
    def "should throw exception on find top population growths when top number = #topNumber"() {
        given:
            populationRepository.findPopulations() >> []
        when:
            populationService.findTopPopulationGrowths(topNumber)
        then:
            thrown IllegalArgumentException
        where:
            topNumber << [-2, -1, 0]
    }

    @Unroll
    def "should find top 2 population declines when populations #populations"() {
        given:
            populationRepository.findPopulations() >> populations
        when:
            List<PopulationDifference> populationDeclines = populationService.findTopPopulationDeclines(2)
        then:
            populationDeclines == expectedPopulationDeclines
        where:
            populations                                 | expectedPopulationDeclines
                []                                      | []

                [new Population(2010, 35000)]           | []

                [new Population(2010, 35000),
                 new Population(2012, 25000)]           | []

                [new Population(2010, 35000),
                 new Population(2011, 45000),
                 new Population(2012, 55000),
                 new Population(2013, 65000)]           | []

                [new Population(2010, 35000),
                 new Population(2011, 35000)]           | []

                [new Population(2010, 95000),
                 new Population(2011, 90000),
                 new Population(2012, 80000),
                 new Population(2013, 77000)]           | [new PopulationDifference(2012, -10000), new PopulationDifference(2011, -5000)]

                [new Population(2010, 55000),
                 new Population(2011, 45000),
                 new Population(2012, 35000),
                 new Population(2013, 25000)]           | [new PopulationDifference(2011, -10000), new PopulationDifference(2012, -10000)]

                [new Population(2010, 35000),
                 new Population(2011, 25000),
                 new Population(2012, 55000)]           | [new PopulationDifference(2011, -10000)]


    }

    @Unroll
    def "should throw exception on find top population declines when top number = #topNumber"() {
        given:
            populationRepository.findPopulations() >> []
        when:
            populationService.findTopPopulationDeclines(topNumber)
        then:
            thrown IllegalArgumentException
        where:
            topNumber << [-2, -1, 0]
    }

}
