package com.adthena.population.application;

import com.adthena.population.domain.PopulationDifference;
import com.adthena.population.domain.PopulationService;
import com.adthena.population.infrastructure.PopulationMongoDBRepository;
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PopulationApplication {

    public static final String MONGO_DB_NAME = "population";
    public static final String POPULATION_FILE_PATH = "populations.json";

    public static void main(String[] args) throws IOException {
        DB populationDatabase = recreatePopulationDatabase();
        DBCollection populations = populationDatabase.getCollection("populations");
        loadPopulationsInto(populations);
        PopulationService populationService = new PopulationService(new PopulationMongoDBRepository(populations));
        System.out.println("--- Results ---");
        calculateTop2PopulationGrowthYears(populationService);
        calculateTop2PopulationDeclines(populationService);
        calculateLargestDeviationFromAverageGrowthDifference(populationService);
    }

    private static DB recreatePopulationDatabase() throws UnknownHostException {
        System.out.print("MongoDB - connecting nad recreating " + MONGO_DB_NAME + " database...");
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost"));
        mongoClient.dropDatabase(MONGO_DB_NAME);
        DB db = mongoClient.getDB(MONGO_DB_NAME);
        System.out.println("Ok");
        return db;
    }

    private static void loadPopulationsInto(DBCollection populations) throws IOException {
        System.out.print("Loading populations from JSON file...");
        Stream<String> lines = Files.lines(Paths.get(POPULATION_FILE_PATH), Charset.defaultCharset());
        lines.forEach(line -> {
                    DBObject dbObject = (DBObject) JSON.parse(line);
                    populations.insert(dbObject);
                }
        );
        System.out.println("Ok");
    }

    private static void calculateTop2PopulationGrowthYears(PopulationService populationService) {
        System.out.print("The top 2 population growth years: ");
        List<PopulationDifference> topPopulationGrowths = populationService.findTopPopulationGrowths(2);
        topPopulationGrowths.stream().forEach(populationGrowth -> System.out.print(populationGrowth.getYear() + " "));
        System.out.println();
    }

    private static void calculateTop2PopulationDeclines(PopulationService populationService) {
        System.out.print("The top 2 population declines: ");
        List<PopulationDifference> topPopulationDeclines = populationService.findTopPopulationDeclines(2);
        topPopulationDeclines.stream().forEach(populationDecline -> System.out.print(populationDecline.getValue() + " "));
        System.out.println();
    }

    private static void calculateLargestDeviationFromAverageGrowthDifference(PopulationService populationService) {
        System.out.print("The largest deviation from the average growth difference: ");
        Optional<Double> largestDeviationFromAverageGrowthDifference = populationService.findLargestDeviationFromAverageGrowthDifference();
        System.out.println(largestDeviationFromAverageGrowthDifference.orElse(null));
    }
}
