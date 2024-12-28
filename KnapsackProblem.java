import java.io.*;
import java.util.*;

class Knapsack {
    int capacity;
    List<Item> items;

    Knapsack(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    void addItem(Item item) {
        this.items.add(item);
    }

    int getTotalWeight() {
        return items.stream().mapToInt(item -> item.weight).sum();
    }

    int getTotalProfit() {
        return items.stream().mapToInt(item -> item.profit).sum();
    }
}

class Item {
    int weight;
    int profit;
    double unitProfit;

    Item(int weight, int profit) {
        this.weight = weight;
        this.profit = profit;
        this.unitProfit = (double) profit / weight;
    }
}

public class KnapsackProblem {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please provide the input file path as an argument.");
            return;
        }

        String inputFilePath = args[0];
        List<Item> items = new ArrayList<>();
        int capacity;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            capacity = Integer.parseInt(br.readLine().trim());

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int weight = Integer.parseInt(parts[0]);
                int profit = Integer.parseInt(parts[1]);
                items.add(new Item(weight, profit));
            }
        }

        // Solve using greedy algorithm
        Knapsack greedySolution = solveGreedy(items, capacity);
        int greedyProfit = greedySolution.getTotalProfit();

        // Solve using random solutions
        Random rand = new Random();
        int betterThanGreedy = 0;
        int randomIterations = 1_000_000;
        int minProfit = Integer.MAX_VALUE;
        int maxProfit = Integer.MIN_VALUE;
        long totalProfit = 0;

        for (int i = 0; i < randomIterations; i++) {
            Knapsack randomSolution = solveRandom(items, capacity, rand);
            int randomProfit = randomSolution.getTotalProfit();

            totalProfit += randomProfit;
            minProfit = Math.min(minProfit, randomProfit);
            maxProfit = Math.max(maxProfit, randomProfit);

            if (randomProfit > greedyProfit) {
                betterThanGreedy++;
            }
        }

        double averageProfit = (double) totalProfit / randomIterations;

        // Print results
        System.out.println("The profit of the greedy algorithm: " + greedyProfit);
        System.out.printf("Stats of the profits of the 1 million random solutions: minimum = %d, average = %.2f, maximum = %d\n",
                minProfit, averageProfit, maxProfit);
        System.out.println("Random solutions better than greedy: " + betterThanGreedy + "/" + randomIterations);
    }

    private static Knapsack solveGreedy(List<Item> items, int capacity) {
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort((a, b) -> Double.compare(b.unitProfit, a.unitProfit));

        Knapsack knapsack = new Knapsack(capacity);

        for (Item item : sortedItems) {
            if (knapsack.getTotalWeight() + item.weight <= capacity) {
                knapsack.addItem(item);
            }
        }

        return knapsack;
    }

    private static Knapsack solveRandom(List<Item> items, int capacity, Random rand) {
        List<Item> shuffledItems = new ArrayList<>(items);
        Collections.shuffle(shuffledItems, rand);

        Knapsack knapsack = new Knapsack(capacity);

        for (Item item : shuffledItems) {
            if (knapsack.getTotalWeight() + item.weight <= capacity) {
                knapsack.addItem(item);
            }
        }

        return knapsack;
    }
}
