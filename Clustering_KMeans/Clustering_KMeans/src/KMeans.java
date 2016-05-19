

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

//import com.datafocus.clustering.Point;

public class KMeans {

	//Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 3;    
    //Number of Points
    private int NUM_POINTS;
    private String directory;
    private String fileName;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 255;
    
    private List<Point> points;
    private List<Cluster> clusters;
    
    public KMeans(String directory, String fileName) {
    	this.points = new ArrayList<Point>();
    	this.clusters = new ArrayList<Cluster>();
    	this.directory = directory;
    	this.fileName = fileName;
    	
    	try{
			BufferedReader in = new BufferedReader(new FileReader(directory+fileName));
			String line;
			int x = 0;
			while((line = in.readLine()) != null){
				String arr[] = line.split(" ");
				x++;
				points.add(new Point(Double.parseDouble(arr[0])%this.MAX_COORDINATE,Double.parseDouble(arr[1])%this.MAX_COORDINATE));
			}
			
		}catch(Exception e){
			System.out.println(e);
		}
    	
    	this.NUM_POINTS = this.points.size();
    	
    }
    
    public static void main(String[] args) {
    	
    	KMeans kmeans = new KMeans("C:\\Users\\DELL\\Desktop\\","pixel.txt");
    	kmeans.init();
    	kmeans.calculate();
    }
    
    //Initializes the process
    public void init() {
    	//Create Points
    	//points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
    	
    	//Create Clusters
    	//Set Random Centroids
    	List<Point> centroids = new ArrayList<Point>();
    	try{
			BufferedReader in = new BufferedReader(new FileReader(directory+"output.txt"));
			String line;
			int x = 0;
			while((line = in.readLine()) != null){
				String arr[] = line.split(",");
				x++;
				centroids.add(new Point(Double.parseDouble(arr[0])%this.MAX_COORDINATE,Double.parseDouble(arr[1])%this.MAX_COORDINATE));
			}
			
		}catch(Exception e){
			System.out.println(e);
		}
    	
    	
    	for (int i = 0; i<NUM_CLUSTERS; i++) {
    		Cluster cluster = new Cluster(i);
    		//Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    		//Point centroid = Point.createRandomPoint(this.MIN_COORDINATE, this.MAX_COORDINATE);
    		cluster.setCentroid(centroids.get(i));
    		//cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	//Print Initial state
    	plotClusters();
    }

	private void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	List<Point> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	List<Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i<lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	
        	        	
        	if(distance == 0) {
        		System.out.println("#################");
            	System.out.println("Iteration: " + iteration);
            	System.out.println("Centroid distances: " + distance);
            	plotClusters();
            	clusterImage(clusters,this.directory,"quickbird.jpg");
        		finish = true;
        	}
        }
    }
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private List<Point> getCentroids() {
    	List<Point> centroids = new ArrayList<Point>(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(aux.getX(),aux.getY());
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Point point : points) {
        	min = max;
            for(int i = 0; i< NUM_CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance< min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> list = cluster.getPoints();
            int n_points = list.size();
            
            for(Point point : list) {
            	sumX += point.getX();
                sumY += point.getY();
            }
            
            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
            	double newX = sumX / n_points;
            	double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
    
    private void clusterImage(List<Cluster> clusters,String dir,String fName){
    	int height = 0;
    	int width = 0;
    	BufferedImage image = null;
    	
    	try{
    		image = ImageIO.read(new File(dir+fName));
    		height = image.getHeight();
    		width = image.getWidth();
    		
    	}catch(Exception e){
    		System.out.println(e);
    		e.printStackTrace();
    	}
    	int count = 0;
    	for(Point x : this.points){
    		int cluster = x.getCluster();
    		int row;
    		int column;
    		if(height > width){
    			row = count/height;
    			column = count%height;
    		}else if(height < width){
    			row = count/width;
    			column = count %width;
    		}else{
    			row = count/width;
    			column = count%width;
    		}
    		if(cluster == 0){
    			Color c = new Color(255,0,0);
    			image.setRGB(column, row,c.getRGB());
    		}else if(cluster == 1){
    			Color c = new Color(0,255,0);
    			image.setRGB(column, row,c.getRGB());
    		}else if(cluster == 2){
    			Color c = new Color(0,0,255);
    			image.setRGB(column, row,c.getRGB());
    		}
    		count++;
    	}
    	
    	File fileDest = new File(dir+"Result"+fName);
    	try{
    		ImageIO.write(image, "jpg",fileDest);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}