package chabernac.space.geom;


public class Line2D {
	private Point2D myStartPoint = null;
	private Point2D myEndPoint = null;
	private Vector2D myDirection = null;
  private float myLength = 0;
	
	public Line2D(Point2D p1, Vector2D aVector){
		myStartPoint = p1;
		myDirection = aVector;
		myEndPoint = new Point2D(p1.x + aVector.x, p1.y + aVector.y);
	}
	
	public Line2D(Point2D p1, Point2D p2){
		myStartPoint = p1;
		myEndPoint = p2;
		myDirection = new Vector2D(p1, p2);
	}
	
	public Point2D getStartPoint(){
		return myStartPoint;
	}
	
	public Vector2D getDirection(){
		return myDirection;
	}
	
	public int intersect(Line2D aLine){
		return -1;
	}
	
	public float intersectHorizontalLine(float y){
		return  (y - myStartPoint.y) / (myEndPoint.y - myStartPoint.y);
	}
	
	public Point2D getPoint(float aTime){
		return new Point2D(myStartPoint.x + aTime * myDirection.x, myStartPoint.y  +  aTime * myDirection.y);
	}
  
  public float length(){
    if(myLength == 0){
      float xDiff = myEndPoint.x - myStartPoint.x;
      float yDiff = myEndPoint.y - myStartPoint.y;
      myLength = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    return myLength;
  }
	
	public static void main(String args[]){
		Line2D theLine = new Line2D(new Point2D(0,0), new Point2D(100,100));
		float time = theLine.intersectHorizontalLine(50);
		Point2D thePoint = theLine.getPoint(time);
		
	}

}
