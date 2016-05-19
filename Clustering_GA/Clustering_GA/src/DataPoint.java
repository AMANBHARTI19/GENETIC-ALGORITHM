
public class DataPoint {
    public double [] dimensions;
    public int numDimensions;

    public DataPoint() {
    }
    
    DataPoint(int numDimensions)
    {
        dimensions = new double[numDimensions];
        
        this.numDimensions = numDimensions;
    }
    
    DataPoint(DataPoint datapoint)
    {
        this.dimensions = new double[datapoint.numDimensions];
        System.arraycopy(datapoint.dimensions, 0, this.dimensions, 0, datapoint.numDimensions);
    }
    
    public boolean isSame(DataPoint point)
    {
        for(int i = 0; i < numDimensions; i++)
        {
            if(this.dimensions[i] != point.dimensions[i])
                return false;
        }
        
        return true;
    }
    
    @Override public DataPoint clone()
    {
        DataPoint clone = new DataPoint();
        clone.numDimensions = this.numDimensions;
        clone.dimensions = new double[numDimensions];
        System.arraycopy(this.dimensions, 0, clone.dimensions, 0, numDimensions);
        
        return clone;
    }
}