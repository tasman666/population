package com.adthena.population.infrastructure;

import com.adthena.population.domain.Population;
import com.adthena.population.domain.PopulationRepository;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

public class PopulationMongoDBRepository implements PopulationRepository {
    private DBCollection populationsCollection;

    public PopulationMongoDBRepository(DBCollection populationsCollection) {
        this.populationsCollection = populationsCollection;
    }

    @Override
    public List<Population> findPopulations() {
        List<Population> populations = new ArrayList<>();
        AggregationOutput output = populationsCollection.aggregate(
                new BasicDBObject("$group",
                        new BasicDBObject("_id", "$year")
                                .append("totalPopulation",
                                        new BasicDBObject("$sum", "$population"))));
        output
                .results()
                .forEach(result -> populations.add(createPopulation(result)));
        return populations;
    }

    private Population createPopulation(DBObject result) {
        return new Population((Integer)result.get("_id"), (Integer)result.get("totalPopulation"));
    }
}
