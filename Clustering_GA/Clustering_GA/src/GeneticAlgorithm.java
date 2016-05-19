
import java.util.Random;

public class GeneticAlgorithm {
    int numIterations;
    // the maximum number of iterations that the GA can complete
    
    int populationSize;
    // number of chromosomes
    
    Chromosome [] chromosomes;
    // the population
    
    int selectionType;
    // 1 = roulette wheel
    // 2 = tournament selection
    
    int tournamentSize;
    // if tournament selection is used
    
    int crossoverType;
    // 1 = one-point
    // 2 = arithmetic
    
    int mutationType;
    // 1 = swap
    // 2 = guassian noise
    
    double [] fitnessProgress;
    double [] intraDistanceProgress;
    double [] interDistanceProgress;
    
    double crossoverProbablity;
    
    int numClusters;
    int numDimensions;
    int dataSetLength;
    DataPoint [] dataSet;
            
    public GeneticAlgorithm() {
    }

    public GeneticAlgorithm(int numIterations, int populationSize, int selectionType, int tournamentSize, int crossoverType, int mutationType, int dataSetLength_, int numDimensions_, int numClusters_, DataPoint [] dataSet_, int centroidInitializationType_, double crossoverProbablity) {
        chromosomes = new Chromosome[populationSize];
                
        for(int i = 0; i < populationSize; i++)
        {
            chromosomes[i] = new Chromosome(dataSetLength_, numDimensions_, numClusters_, dataSet_, centroidInitializationType_);         
        }
        
        this.numIterations = numIterations;                
        this.populationSize = populationSize;
        this.selectionType = selectionType;
        this.tournamentSize = tournamentSize;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;       
        this.numClusters = numClusters_;
        this.numDimensions = numDimensions_;
        this.dataSetLength = dataSetLength_;
        this.dataSet = dataSet_;
        this.crossoverProbablity = crossoverProbablity;
        
        this.fitnessProgress = new double[numIterations];
        this.intraDistanceProgress = new double[numIterations];
        this.interDistanceProgress = new double[numIterations];
    }
    
    public Chromosome selectParent()
    {
        if(selectionType == 1)
        {
            return this.rouletteWheelSelect();
        }
        else
        {
            return this.tournamentSelect();
        }
    }
    
   
    public Chromosome getCrossover()
    {
        if(crossoverType == 1)
            return onePointCrossover(selectParent(), selectParent());
        else
            return arithmeticCrossover(selectParent(), selectParent());
    }
    
    public void doMutation(Chromosome chromosome)
    {
        if(mutationType == 1)
        {
            swapMutation(chromosome);
        }
        else
        {
            guassianMutation(chromosome);
        }
    }
    
    private void guassianMutation(Chromosome mutator)
    {
        int rangeMin = 0, rangeMax = numClusters;
        
        Random r = new Random();                
        int randomValue = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
        if(randomValue < 0) randomValue *= -1;
        randomValue %= rangeMax - 1;    
        
        DataPoint mutatorCentroid = mutator.centroids[randomValue];
        for(int i = 0; i < numDimensions; i++)
        {
            double max = mutator.getSearchSpaceMax()[i];
            double min = mutator.getSearchSpaceMin()[i];
            if(mutatorCentroid.dimensions[i] == max || mutatorCentroid.dimensions[i] == min)
            {
                return;
            }
            double gaussian = getNextGaussian();
            while(mutatorCentroid.dimensions[i] + gaussian < min || gaussian + mutatorCentroid.dimensions[i] > max)
            {
                gaussian = getNextGaussian();
            }
            mutatorCentroid.dimensions[i] += gaussian;
        }
    }
    
    private double getNextGaussian()
    {
        Random r = new Random();
        double v1, v2, s;
        do {
            v1 = (2 * r.nextDouble() - 1) * 0.1;   // between -0.1 and 0.1
            v2 = (2 * r.nextDouble() - 1) * 0.1;   // between -0.1 and 0.1
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        //nextNextGaussian = v2 * multiplier;
        //haveNextNextGaussian = true;
        return v1 * multiplier;        
    }
    
    private Chromosome onePointCrossover(Chromosome parent1, Chromosome parent2)
    {
        Chromosome parent1_clone = parent1.clone();
        Chromosome parent2_clone = parent2.clone();
        
        // first, choose random centroid
        int rangeMin = 0, rangeMax = numClusters;
        
        Random r = new Random();                
        int randomValue = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
        if(randomValue < 0) randomValue *= -1;
        randomValue %= rangeMax - 1;                    
        
        // then swap centroid between parents        
        DataPoint temp = parent1_clone.centroids[randomValue].clone();
        parent1_clone.centroids[randomValue] = parent2_clone.centroids[randomValue].clone();
        parent2_clone.centroids[randomValue] = temp.clone();
        
        // finally, return strongest of changed parents
        double QE = parent1_clone.getFitness();
        if(QE > parent2_clone.getFitness())
        {
            return parent1_clone;
        }
        else return parent2_clone;        
    }
    
    private Chromosome arithmeticCrossover(Chromosome parent1, Chromosome parent2)
    {
        DataPoint [] centroids = new DataPoint[numClusters];

        for(int x = 0; x < numClusters; x++)
        {
            centroids[x] = new DataPoint(numDimensions);
            for(int y = 0; y < numDimensions; y++)
            {
                centroids[x].dimensions[y] = (parent1.centroids[x].dimensions[y] + parent2.centroids[x].dimensions[y]) / 2;
                // set new centroids' dimensions equal to average of parents' centroids' dimensions
            }
            
        }
        
        return new Chromosome(dataSetLength, numDimensions, numClusters, dataSet, centroids);
    }
    
    private void swapMutation(Chromosome mutator)
    {
        // first, select two random centroids
        int rangeMin = 0, rangeMax = numClusters;
        
        Random r = new Random();                
        int randomValue1 = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
        if(randomValue1 < 0) randomValue1 *= -1;
        randomValue1 %= rangeMax - 1;   
        
        DataPoint swap1 = mutator.centroids[randomValue1];
        
        r = new Random();      
        
        int randomValue2 =  randomValue1;
        while(randomValue2 == randomValue1)
        {
            randomValue2 = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
            if(randomValue2 < 0) randomValue2 *= -1;
            randomValue2 %= rangeMax - 1;   
        }
        
        DataPoint swap2 = mutator.centroids[randomValue2];
                        
        // then select a random dimension
        rangeMax = numDimensions;
        
        r = new Random();                
        int randomValue = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
        if(randomValue < 0) randomValue *= -1;
        randomValue %= rangeMax - 1;
        
        // then swap the values of the dimension between the 2 centroids
        double temp = swap1.dimensions[randomValue];        
        swap1.dimensions[randomValue] = swap2.dimensions[randomValue];
        swap2.dimensions[randomValue] = temp;

    }
    
    public double [] getFitnessProgress()
    {
        return fitnessProgress;
    }
    
    public double [] getIntraProgress()
    {
        return intraDistanceProgress;
    }    
    
    public double [] getInterProgress()
    {
        return interDistanceProgress;
    }    
    
    public Chromosome getMe()
    {
        Chromosome temp;
        Chromosome [] newPopulation = new Chromosome[populationSize];
        // the new generation of chromosomes
        
        int newPopulationIndex;
        // how many children there are in the new population
        
        double probablityIndex = 1.0 / (numIterations + 0.1);
        double probability = 1.0;
        
        
       
        
        for(int i = 0; i < numIterations; i++)
        {
            newPopulation[0] = getBestParent();

            fitnessProgress[i] = newPopulation[0].getFitness(); 
            intraDistanceProgress[i] = newPopulation[0].getIntraClusterDistance();
            interDistanceProgress[i] = newPopulation[0].getInterClusterDistance();
            // copy the best parent into the next generation
            // this ensures that the generation can only improve
            
            newPopulationIndex = 1;
            
            Random r;
            
            while(newPopulationIndex <= populationSize - 1)
            {
                
                
                //                CROSSOVER
                // *********************************************
                    r = new Random();                
                    double randomValue = r.nextDouble();        
                    if(randomValue < 0) randomValue *= -1;
                    randomValue %= 1;                     
                    
                    if(randomValue <= probability)
                    {     
                        newPopulation[newPopulationIndex] = new Chromosome(chromosomes[newPopulationIndex]);
                    }   
                    
                    temp = getCrossover();
                    newPopulation[newPopulationIndex] = new Chromosome(temp);
                    // copy newly created child into new generation                                        
                    
               // **********************************************
                    
                    
                    
                    
                    
                    
               //                MUTATION
               // *********************************************
                    
                    r = new Random();                
                    randomValue = r.nextDouble();        
                    if(randomValue < 0) randomValue *= -1;
                    randomValue %= 1;                     
                    
                    if(randomValue >= crossoverProbablity)
                    {
                        doMutation(newPopulation[newPopulationIndex]);
                    }
                    
                        
              //  ************************************************
                    
                    
                    newPopulationIndex++;
            }
            // fill up the rest of the new population with new chromosomes
            // the new chromsomes are all the product of crossover
            
            for(int j = 0; j < populationSize; j++)
            {
                chromosomes[j] = new Chromosome(newPopulation[j]);
            }
            // set old population (chromosomes) to new population (newPopulation)
            probability -= probablityIndex; 
        }
            
        return getBestParent();
    }
    
    private Chromosome rouletteWheelSelect()
    {
        double sumFitness = 0;
        double [] fitnessArray = new double[populationSize];
        double fitness;
        
        for(int i = 0; i < populationSize; i++)
        {
            fitness = chromosomes[i].getFitness();
            sumFitness += fitness;
            fitnessArray[i] = fitness;
        }
        
        double rangeMin = 0, rangeMax = sumFitness;
        
        Random r = new Random();                
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();        
        if(randomValue < 0) randomValue *= -1;
        randomValue %= rangeMax - 1;
        
        for(int i = 0; i < populationSize; i++)
        {
            if(randomValue <= fitnessArray[i])
            {
                return chromosomes[i];
            }
            else
            {
                randomValue -= fitnessArray[i];
            }
        }
        
        return chromosomes[populationSize - 1];
        // this shouldn't actually ever happen
    }
    
    private Chromosome tournamentSelect()
    {
        Chromosome [] contestants = new Chromosome[tournamentSize];
        
        // randomly select chromosomes to take part in tournament
        for(int i = 0; i < tournamentSize; i++)
        {
            int rangeMin = 0, rangeMax = populationSize;
            
            Random r = new Random();                
            int randomValue = rangeMin + (rangeMax - rangeMin) * r.nextInt();        
            if(randomValue < 0) randomValue *= -1;
            randomValue %= rangeMax - 1;    
            
            contestants[i] = chromosomes[randomValue];
        }
        
        // select best constestant
        double max = Integer.MIN_VALUE;
        double fitness;
        int fittestIndex = 0;
        for(int i = 0; i < tournamentSize; i++)
        {
            fitness = contestants[i].getFitness();
            if(fitness > max)
            {
                max = fitness;
                fittestIndex = i;
            }
        }
        
        return contestants[fittestIndex];
    }
    
    private Chromosome getBestParent()
    {
        double max = Integer.MIN_VALUE;
        double fitness;
        int fittestIndex = 0;
        for(int i = 0; i < populationSize; i++)
        {
            fitness = chromosomes[i].getFitness();
            if(fitness > max)
            {
                max = fitness;
                fittestIndex = i;
            }
        }        
        
        return chromosomes[fittestIndex];
    }
    
    
}