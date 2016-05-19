
import java.util.Random;

public class Chromosome {

    int dataSetLength, numDimensions;
    int numClusters;    

    DataPoint [] dataSet = new DataPoint[dataSetLength];    
    DataPoint [] centroids = new DataPoint[numClusters];    
    
    public Chromosome() {
    }
    
    @Override public Chromosome clone()
    {
        Chromosome clone = new Chromosome();
        clone.dataSetLength = this.dataSetLength;
        clone.numDimensions = this.numDimensions;
        clone.numClusters = this.numClusters;
        clone.dataSet = this.dataSet.clone();
        clone.centroids = this.centroids.clone();
        
        return clone;
    }
    
    public Chromosome(Chromosome chromosome)
    {
        this.dataSetLength = chromosome.dataSetLength;
        this.numDimensions = chromosome.numDimensions;
        this.numClusters = chromosome.numClusters;
        
        this.dataSet = new DataPoint[dataSetLength];
        this.centroids = new DataPoint[numClusters];
        
        for(int y = 0; y < dataSetLength; y++)
        {
            this.dataSet[y] = new DataPoint(numDimensions);
            System.arraycopy(chromosome.dataSet[y].dimensions, 0, this.dataSet[y].dimensions, 0, numDimensions);
        }
        
        for(int y = 0; y < numClusters; y++)
        {
            this.centroids[y] = new DataPoint(numDimensions);
            System.arraycopy(chromosome.centroids[y].dimensions, 0, this.centroids[y].dimensions, 0, numDimensions);
        }        
    }

    // this constructors takes in all the values for its member variables
    public Chromosome(int dataSetLength_, int numDimensions_, int numClusters_, DataPoint [] dataSet_, DataPoint [] centroids_) {
        this.dataSetLength = dataSetLength_;
        this.numDimensions = numDimensions_;
        this.numClusters = numClusters_;
        //this.dataSet = dataSet_;
        //this.centroids = centroids_;
        
        this.dataSet = new DataPoint[dataSetLength_];
        this.centroids = new DataPoint[numClusters_];
        
        for(int i = 0; i < dataSetLength; i++)
        {
            dataSet[i] = new DataPoint(numDimensions);
            dataSet[i] = dataSet_[i];
        }
        // initialize dataSet
        
        for(int i = 0; i < numClusters; i++)
        {
            centroids[i] = new DataPoint(numDimensions);
            centroids[i] = centroids_[i];
        }        
        // initialize centroids
    }
    
    // this constructors takes in all the values for its member variables, except the centroids, which it will initialize
    // how it will initializa the centroids depends on the centroidInitializationType
    public Chromosome(int dataSetLength_, int numDimensions_, int numClusters_, DataPoint [] dataSet_, int centroidInitializationType) {
        this.dataSetLength = dataSetLength_;
        this.numDimensions = numDimensions_;
        this.numClusters = numClusters_;
        //this.dataSet = dataSet_;
        //this.centroids = centroids_;
        
        this.dataSet = new DataPoint[dataSetLength_];
        this.centroids = new DataPoint[numClusters_];
        
        for(int i = 0; i < dataSetLength; i++)
        {
            dataSet[i] = new DataPoint(numDimensions);
            dataSet[i] = dataSet_[i];
        }
        // initialize dataSet
        
        centroids = new DataPoint[numClusters_];
        
        // centroidInitializationType == 1 means randomly initializa centroids within search space
        if(centroidInitializationType == 1)
        {
            double [] rangeMin = getSearchSpaceMin();
            double [] rangeMax = getSearchSpaceMax();

            for(int y = 0; y < numClusters; y++)    
            {
                centroids[y] = new DataPoint(numDimensions);
                centroids[y].numDimensions = numDimensions;

                for(int x = 0; x < numDimensions; x++)
                {
                    Random r = new Random();                
                    double randomValue = rangeMin[x] + (rangeMax[x] - rangeMin[x]) * r.nextDouble();                

                    this.centroids[y].dimensions[x] = randomValue;
                }
            }              
        }
        // 2 means shuffle data and set centroid equal to randomly selected data point
        else if(centroidInitializationType == 2)
        {
            int rangeMin = 0;
            int rangeMax = dataSetLength_ - 1;
            
            // randomly choose data points and set centroids to their position
            for(int i = 0; i < numClusters; i++)
            {
                    Random r = new Random();                
                    int randomValue = rangeMin + (rangeMax - rangeMin) * r.nextInt();
                    if(randomValue < 0) randomValue *= -1;
                    randomValue %= dataSetLength - 1;                    
                     //randomly select point in the dataSet
                                        
                    centroids[i] = new DataPoint(numDimensions);
                    centroids[i].numDimensions = numDimensions;                    
                    
                    centroids[i].dimensions = dataSet[randomValue].dimensions;
                System.arraycopy(dataSet[randomValue].dimensions, 0, centroids[i].dimensions, 0, numDimensions);
            }
        }
    }    
    
    public Chromosome(int numClusters_, int numDimensions_, DataPoint [] centroids_)
    {
        this.numClusters = numClusters_;
        this.numDimensions = numDimensions_;
        
        this.centroids = new DataPoint[numClusters_];
        
        for(int i = 0; i < numClusters_; i++)
        {
            this.centroids[i] = new DataPoint(numDimensions_);
            this.centroids[i] = centroids_[i];
        }
    }
    
    
    // this function returns the centroid with the lowest euclidian distance from "feature"
    public DataPoint getNearestCentroid(DataPoint feature, DataPoint[] centroids_)
    {
        double nearest = Integer.MAX_VALUE;
        int nearestIndex = 0;
        
        for(int i = 0; i < numClusters; i++)
        {
            double distance = GA_Clustering.getEuclidianDistance(feature, centroids_[i], numDimensions);
            if(distance < nearest)
            // if a closer centroid has been found
            {
                nearest = distance;
                nearestIndex = i;
            }
        }
        
        return centroids_[nearestIndex];
        // return the nearest centroid
    }
    
    public double getFitness()
    {
        return getIntraClusterDistance() / getInterClusterDistance();
        // maximize distances between centroids
        // minimize distances between data points and their centroids
    }
    
    public double getInterClusterDistance()
    {
        double euclidianDistances = 0;
        
 
        int y;

        for(y = 0; y < dataSetLength; y++)
        {
            euclidianDistances += GA_Clustering.getEuclidianDistance(dataSet[y], getNearestCentroid(dataSet[y], centroids), numDimensions);
            // get each data point's euclidian distance to its nearest centroid
        }
        
        return euclidianDistances / y;
    }
    
    public double getIntraClusterDistance()
    {
        double euclidianDistances = 0;
        int c = 0;
        for(int i = 0; i < numClusters; i++)
        {
            for(int j = 0; j < numClusters; j++)
            {
                euclidianDistances += GA_Clustering.getEuclidianDistance(centroids[i], centroids[j], numDimensions);
                c++;
            }
        }
        return euclidianDistances / c;
    }
    
    // this function returns an array containing
    // the max values for each dimension in the
    // search space
    public double[] getSearchSpaceMax()
    {
        double max[] = new double[numDimensions];
        
        for(int i = 0; i < numDimensions; i++)
        {
            max[i] = Double.MIN_VALUE;
        }
        // initialize max dimensions to smallest values
        
        for(int y = 0; y < dataSetLength; y++)
        {
            for(int x = 0; x < numDimensions; x++)
            {
                if(dataSet[y].dimensions[x] > max[x])
                {
                    max[x] = dataSet[y].dimensions[x];
                }
            }
        }
        
        return max;
    }
    
    // this function returns an array containing
    // the min values for each dimension in the
    // search space
    public double[] getSearchSpaceMin()
    {
        double min[] = new double[numDimensions];
        
        for(int i = 0; i < numDimensions; i++)
        {
            min[i] = Double.MAX_VALUE;
        }
        // initialize min dimensions to smallest values
        
        for(int y = 0; y < dataSetLength; y++)
        {
            for(int x = 0; x < numDimensions; x++)
            {
                if(dataSet[y].dimensions[x] < min[x])
                {
                    min[x] = dataSet[y].dimensions[x];
                }
            }
        }
        
        return min;
    }    
    
    
}
