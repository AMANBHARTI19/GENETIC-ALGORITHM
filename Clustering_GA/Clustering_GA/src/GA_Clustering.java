
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


import javax.swing.JOptionPane;

//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartFrame;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;


public class GA_Clustering {
    
    public static double getEuclidianDistance(DataPoint first, DataPoint second, int numDimensions)
    {
        double sum = 0;
        for(int i = 0; i < numDimensions; i++)
        {
            sum += Math.pow(first.dimensions[i] - second.dimensions[i], 2);
            // (first[i] - second[i])^2
        }       
        return Math.sqrt(sum);
    }    


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {   
    	String everything;
        String sb;
        int numLines = 0;
        BufferedReader br = new BufferedReader(new FileReader(JOptionPane.showInputDialog("Enter the name of the data file\nExample: data.txt")));
        try {
          //  StringBuilder sb = new StringBuilder();
                     sb="";
            		String line =br.readLine();

            while (line != null) {
                sb=sb + line;
                sb=sb + "\n";
                line = br.readLine();
                numLines++;
               
            }	
           // System.out.println(numLines);
         
        // everything = sb.toString();
            
        } finally {
            br.close();
        }        
       
       
        ArrayList<String> words = new ArrayList<String>();
        StringTokenizer tk = new StringTokenizer(sb);
        
        int num = 0;
        while(tk.hasMoreTokens())
        {
            num++;
            words.add(tk.nextToken());            
        }
       // System.out.println(num);
        //System.exit(0);
        int dataSetLength = numLines;
        // number of features
        

        int numDimensions = num / numLines;
        // number of dimensions of problem space
        
        int numClusters = Integer.parseInt(JOptionPane.showInputDialog("Enter the amount of clusters"));
        // the number of centroids
        
        int populationSize = Integer.parseInt(JOptionPane.showInputDialog("Enter the population size"));
        // number of chromosomes
       
        int centroidInitializationType = Integer.parseInt(JOptionPane.showInputDialog("Enter the centroid initialization type\n1 = random initialization, 2 = randomly select pattern"));
        // 1 = random initialization
        // 2 = randomly select existing data point

        int selectionType = Integer.parseInt(JOptionPane.showInputDialog("Enter the selection type\n1 = roulette wheel, 2 = tournament selection"));
        // 1 = roulette wheel
        // 2 = tournament selection

        int tournamentSize = Integer.parseInt(JOptionPane.showInputDialog("Enter the tournament size (if you chose roulette wheel, just enter any integer value)"));
        // if tournament selection is used

        int crossoverType = Integer.parseInt(JOptionPane.showInputDialog("Enter the crossover type\n1 = one-point crossover, 2 = arithmetic crossover"));
        // 1 = one-point
        // 2 = arithmetic

        int mutationType = Integer.parseInt(JOptionPane.showInputDialog("Enter the mutation type\n1 = swap mutation, 2 = gaussian noise mutation"));
        // 1 = swap
        // 2 = guassian noise

        int numIterations = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of iterations the GA should complete"));
        // the maximum number of iterations that the GA can complete

        double crossoverProbablity = Double.parseDouble(JOptionPane.showInputDialog("Enter the crossover probability"));
        // the probability that crossover will take place
    
    
        DataPoint [] dataSet = new DataPoint[dataSetLength];
        // data to cluster
             
        int c = 0;
        for(int y = 0; y < dataSetLength; y++)    
        {
            dataSet[y] = new DataPoint(numDimensions);
            dataSet[y].numDimensions = numDimensions;

            for(int x = 0; x < numDimensions; x++)
            {
                dataSet[y].dimensions[x] = Double.valueOf(words.get(c));
                c++;
            }
        }
        
        GeneticAlgorithm GA = new GeneticAlgorithm(numIterations, populationSize, selectionType, tournamentSize, crossoverType, mutationType, dataSetLength, numDimensions, numClusters, dataSet, centroidInitializationType, crossoverProbablity);
        
        Chromosome finalChromosome = GA.getMe();
        
        if(numDimensions <= 3)
        {        
            for(int y = 0; y < numClusters; y++)
            {
                for(int x = 0; x < numDimensions; x++)
                {
                    System.out.print(finalChromosome.centroids[y].dimensions[x] + ",");
                }
                System.out.println();
            }
        }
        
//        System.out.println("Fitness: " + finalChromosome.getFitness());
//        System.out.println("Intra-Cluster Distance: " + finalChromosome.getIntraClusterDistance());
//        System.out.println("Inter-Cluster Distance: " + finalChromosome.getInterClusterDistance());        
//        
//        if(numDimensions == 2)
//        {
//            XYSeriesCollection result = new XYSeriesCollection();
//            XYSeries series2 = new XYSeries("Centroids");
//            
//            XYSeries seriesFeatures[] = new XYSeries[numClusters];
//            
//            
//            for(int y = 0; y < numClusters; y++)
//            {
//                seriesFeatures[y] = new XYSeries("Features" + y);
//            }
//            
//            
//            for(int y = 0; y < dataSetLength; y++)
//            {
//                    DataPoint nearestCentroid = finalChromosome.getNearestCentroid(dataSet[y], finalChromosome.centroids);
//                    for(int i = 0; i < numClusters; i++)
//                    {
//                        if(nearestCentroid.isSame(finalChromosome.centroids[i]))
//                        {
//                            seriesFeatures[i].add(finalChromosome.dataSet[y].dimensions[0], finalChromosome.dataSet[y].dimensions[1]);
//                        }
//                    }
//            }
//            
//
//            
//            for(int y = 0; y < numClusters; y++)
//            {            
//                series2.add(finalChromosome.centroids[y].dimensions[0], finalChromosome.centroids[y].dimensions[1]); 
//            }
//            
//            result.addSeries(series2);
//            for(int i = 0; i < numClusters; i++)
//            {
//                result.addSeries(seriesFeatures[i]);
//            }
//            
//            
//            JFreeChart chart = ChartFactory.createScatterPlot(
//                "Scatter Plot", // chart title
//                "X", // x axis label
//                "Y", // y axis label
//                result, // data
//                PlotOrientation.VERTICAL,
//                true, // include legend
//                true, // tooltips
//                false // urls
//                );
//
//            // create and display a frame...
//            ChartFrame frame = new ChartFrame("Clustering", chart);
//            frame.pack();
//            frame.setVisible(true);      
//        
//        }
//        
//        
//        XYSeriesCollection resultFitness = new XYSeriesCollection();
//        XYSeriesCollection resultClustering = new XYSeriesCollection();
//        
//        XYSeries seriesFitness = new XYSeries("Fitness Function");
//        XYSeries seriesIntra = new XYSeries("Intra Cluster Distance");
//        XYSeries seriesInter = new XYSeries("Inter Cluster Distance");
//        
//            double [] fitnessProgress = GA.getFitnessProgress();
//            
//            for(int i = 0; i < numIterations; i++)
//            {
//                seriesFitness.add(i, fitnessProgress[i]);
//            }        
//            
//            
//            double [] intraProgress = GA.getIntraProgress();
//            
//            for(int i = 0; i < numIterations; i++)
//            {
//                seriesIntra.add(i, intraProgress[i]);
//            }        
//            
//            double [] interProgress = GA.getInterProgress();
//            
//            for(int i = 0; i < numIterations; i++)
//            {
//                seriesInter.add(i, interProgress[i]);
//            }        
//                        
//            
//            
//            resultFitness.addSeries(seriesFitness);            
//            resultClustering.addSeries(seriesIntra);  
//            resultClustering.addSeries(seriesInter);  
//            
//            
//            
//        
//        JFreeChart chart2 = ChartFactory.createXYLineChart(
//            "Scatter Plot", // chart title
//            "X", // x axis label
//            "Y", // y axis label
//            resultFitness, // data
//            PlotOrientation.VERTICAL,
//            true, // include legend
//            true, // tooltips
//            false // urls
//            );
//
//        // create and display a frame...
//        ChartFrame frame2 = new ChartFrame("Fitness Progress", chart2);
//        frame2.pack();
//        frame2.setVisible(true);        
//        
//        JFreeChart chart3 = ChartFactory.createXYLineChart(
//            "Scatter Plot", // chart title
//            "X", // x axis label
//            "Y", // y axis label
//            resultClustering, // data
//            PlotOrientation.VERTICAL,
//            true, // include legend
//            true, // tooltips
//            false // urls
//            );
//
//        // create and display a frame...
//        ChartFrame frame3 = new ChartFrame("Clustering", chart3);
//        frame3.pack();
//        frame3.setVisible(true);           
//        
    }
}