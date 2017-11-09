package chabernac.space.geom;

//import chabernac.utils.Debug;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.math.MatrixException;
import chabernac.space.Camera;
import chabernac.space.Frustrum;
import chabernac.space.PolygonException;
import chabernac.space.Vertex;
import chabernac.space.iTransformator;
import chabernac.space.iTranslatable;
import chabernac.space.shading.iPixelShader;
import chabernac.space.texture.Texture2;
import chabernac.space.texture.TextureFactory;
import chabernac.space.texture.TextureImage;
import chabernac.utils.ArrayTools;

public class Polygon implements iTranslatable{
  private static Logger LOGGER = Logger.getLogger(Polygon.class);

  private static final int TRIANGULATION_THRESHOLD = 1000;

  private int mySize;
  public int myCamSize;
  public Vertex w[];
  public Vertex c[];
  public GVector myNormalVector = null;
  public GVector myNormalCamVector = null;
  private int myCurrentVertex = 0;
  private boolean isConvex;
  private boolean isOptimized;
  public Point3D myCenterPoint = null;
  public Point3D myCamCenterPoint = null;
  public Color color = new Color(0,0,0);
  public int myGrowSize = 10;
  public boolean visible = true;
  private Texture2 myTexture = null;
  private TextureImage myTextureImage = null;
  private boolean isSphericalTexture;
  public boolean doubleSided = false;
  private String myTextureName = null;
  private boolean isTransparentTexture = true;
  private String myBumpMap = null;
  private iPixelShader[] myPixelShaders = null;

  public Polygon(Vertex... worldVertexes){
    initialize();
    mySize = worldVertexes.length;
    myCurrentVertex = mySize;
    w = worldVertexes;
    c = new Vertex[mySize * 2];
    done();
  }

  public Polygon(int aSize){
    mySize = aSize;
    initialize();
  }

  private void initialize(){
    //TODO testje verwijderen
    //myColor = new Color((int)Math.random() % 255, (int)Math.random() % 255, (int)Math.random() % 255);
    //Debug.log(Polygon.class,"color: " + System.currentTimeMillis() % 255);
    /*
  	int red = Math.abs((int)System.currentTimeMillis()) % 255;
  	int green = Math.abs((int)System.currentTimeMillis()) % 255;
  	int blue = Math.abs((int)System.currentTimeMillis()) % 255;
     */
    int red = (int)(Math.random() * 255);
    int green = (int)(Math.random() * 255);
    int blue = (int)(Math.random() * 255);
    //Debug.log(Polygon.class,"red: "  + red + " green: " + green + " blue: " + blue);
    if(color == null) color = new Color(red, green, blue);
    w = new Vertex[mySize];
    c = new Vertex[mySize * 2];
    clear();
  }

  public void addVertex(Vertex aVertex){
    if(myCurrentVertex >= w.length){
      w = (Vertex[])ArrayTools.growArray(w, myGrowSize);
      c = (Vertex[])ArrayTools.growArray(c, myGrowSize * 2);
      mySize += myGrowSize;
      myCamSize += myGrowSize * 2;
    }
    w[myCurrentVertex++] = aVertex;
  }

  public void done(){
    optimize();
    calculateCenterPoint();
    calculateNormalVector();
    /*
    if(myTextureName != null) {
      calculateTexturePoints();
    }
     */
  }

  public void createTexture(){
    try {
      if(myTexture == null){
        //we take the x vector of the texture parallel to the first 2 points of the polygon
        Point3D theOrigin = w[0].myPoint;
        GVector theXVector = new GVector(w[0].myPoint, w[1].myPoint);
        //now we multiply the x vector with the plane's normal vector to obtain the y vector in the plane and orthogonal with the x vector
        GVector theYVector = myNormalVector.produkt(theXVector);
        
        //create a vector from w[0] to center point
        GVector theValidatorVector = new GVector(w[0].myPoint, myCenterPoint);
        if(theYVector.dotProdukt(theValidatorVector) < 0){
          theXVector = new GVector(w[1].myPoint, w[0].myPoint);
          //now we multiply the x vector with the plane's normal vector to obtain the y vector in the plane and orthogonal with the x vector
          theYVector = myNormalVector.produkt(theXVector);
          theOrigin = w[1].myPoint;
        }
        
//        GVector theInvYVector = myNormalVector.inv().produkt( theXVector );

        theXVector.normalize();
        theYVector.normalize();
//        theInvYVector.normalize();

//        Point3D thePointInPolygon = w[0].myPoint.addition( theYVector );
//        float theDistanceToCenter = distanceToCenter( thePointInPolygon );
//
//        Point3D thePointInPolygon2 = w[0].myPoint.addition( theInvYVector );
//        float theDistanceToCenter2 = distanceToCenter( thePointInPolygon2 );
//
//        if(theDistanceToCenter2 < theDistanceToCenter){
//          theYVector = theInvYVector;
//        }


        if(myTextureName != null){
          myTexture = new Texture2(theOrigin, theXVector, theYVector, myTextureName, isTransparentTexture );
        } else if(myTextureImage != null){
          myTexture = new Texture2(theOrigin, theXVector, theYVector, myTextureImage);
        } else {
          //there is not texture image, but still we create a texture and say that is always have to return the color of the polygon
          //this way we can still use the texture object for calculating camera and real world points for points on the screen
          myTexture = new Texture2(theOrigin, theXVector, theYVector, color.getRGB() );
        }
        if(myBumpMap != null){
          myTexture.setBumpMap(TextureFactory.getBumpMap(myBumpMap));
        }
      }

      myTexture.setSpherical(isSphericalTexture);

      calculateTexturePoints();
    } catch (IOException e) {
      LOGGER.error("Could not load texture: " + myTextureName, e);
    }
  }

  private void calculateTexturePoints(){
    for(int i=0;i<w.length;i++){
      w[i].myTextureCoordinate = myTexture.getTextureCoordinate(w[i]);
    }
  }



  public void optimize(){
    if(myCurrentVertex < mySize){
      Vertex[] theTempVertex = new Vertex[myCurrentVertex];
      System.arraycopy(w, 0, theTempVertex, 0,myCurrentVertex);
      w = theTempVertex;
      theTempVertex = new Vertex[myCurrentVertex * 2];
      System.arraycopy(c, 0, theTempVertex, 0,myCurrentVertex * 2);
      c = theTempVertex;
      mySize = myCurrentVertex;
    }
    isOptimized = true;
  }

  public void clear(){
    for(int i=0;i<mySize;i++){
      w[i] = null;
      c[i] = null;
    }
    myCurrentVertex = 0;
  }

  /*
   * a polygon which is convex can only contain 1 vertex more after clipping
   * to a frustrum, a polygon which is not can almost contain twice as much
   * vertexes after clipping to a frustrum
   * @return convex or not
   */
  public boolean isConvex(){
    return isConvex;
  }

  public void calculateNormalVector() throws PolygonException{
    calculateNormalVector(null, true);
  }

  /*
   * Calculate the normal vector of the polygon, the resulting vector will
   * face away from aCenterPoint.  aCenterPoint could be the center of the
   * sphere the polygon is part of.
   */
  public void calculateNormalVector(Point3D aCenterPoint, boolean toWardsPoint) throws PolygonException{
    if(mySize >= 3){
      if(aCenterPoint != null) myNormalVector = new GVector(w[0].myPoint, w[1].myPoint, w[2].myPoint, aCenterPoint, toWardsPoint);
      //Debug.log(this,"calculatig normal vector with 4 points");
      else myNormalVector = new GVector(w[0].myPoint, w[1].myPoint, w[2].myPoint);
      myNormalVector.normalize();
      //Debug.log(this,"");
    } else {
      throw new PolygonException("Not enough vertexes to calculate normal vector");
    }
  }

  public void calculateCenterPoint(){
    if(myCenterPoint != null) return;
    float x = 0,y = 0,z = 0;
    for(int i=0;i<mySize;i++){
      x += w[i].myPoint.x;
      y += w[i].myPoint.y;
      z += w[i].myPoint.z;
    }
    myCenterPoint = new Point3D(x / mySize, y / mySize, z / mySize);
  }

  public float distanceToCenter(Point3D aPoint){
    return new GVector( myCenterPoint, aPoint ).length();
  }

  public void world2Cam(Camera aCamera) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    myCamSize = 0;
    while(myCamSize < mySize){
      c[myCamSize] = aCamera.world2Cam(w[myCamSize]);
      myCamSize++;
    }
    if(myNormalVector == null)  calculateNormalVector();
    myNormalCamVector = aCamera.world2Cam(myNormalVector);
    myCamCenterPoint = aCamera.world2Cam(myCenterPoint);
    myTexture.world2cam(aCamera);
    //if(myTexture != null) myTexture.world2Cam(aCamera);
  }

  /*
   * method to move a polygon in world space
   *
   */
  /*
  public void translate(Camera aCamera) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    int i = 0;
    while(i < mySize){
      w[i] = aCamera.world2Cam(w[i]);
      i++;
    }
    if(myNormalVector == null)  calculateNormalVector();
    //TODO shoud this not be if else?
    else myNormalVector = aCamera.world2Cam(myNormalVector);
    myCenterPoint = aCamera.world2Cam(myCenterPoint);
  }
   */

  public void translate(iTransformator aTransformator) throws PolygonException, MatrixException{
    if(!isOptimized) optimize();
    int i = 0;
    while(i < mySize){
      w[i] = aTransformator.transform(w[i]);
      i++;
    }
    if(myNormalVector == null)  {
      calculateNormalVector();
    }
    myNormalVector = aTransformator.transform(myNormalVector);
    myCenterPoint = aTransformator.transform(myCenterPoint);
    myTexture.translate(aTransformator);
  }

  public void clip2Plane(Plane aPlane) throws PolygonException{
    Vertex[] theTempVertexes = new Vertex[c.length];

    float dist1, dist2; // distances of points to plane
    float distratio; // fraction of distance between two points

    int ii, j=0;

    for(int i=0;i<myCamSize;i++){
      ii = (i+1) % myCamSize;
      dist1 = aPlane.distanceToPoint(c[i].myPoint);
      //System.out.println("Distance of point: " + c[i].myPoint.toString() + " to plane: " + aPlane.toString() + ": " + dist1);
      dist2 = aPlane.distanceToPoint(c[ii].myPoint);
      //System.out.println("Distance of point: " + c[ii].myPoint.toString() + " to plane: " + aPlane.toString() + ": " + dist2);
      //System.out.println("Dist1: " + dist1 + " dist2: " + dist2);
      if(dist1 < 0 && dist2 < 0){
        //both ends are on the wrong side of the plane, do nothing, the points will be discarded
      } else if(dist1 >= 0 && dist2 >= 0){
        //both ends are on the right side of the plane, copy the first point to the temp vertexes array
        theTempVertexes[j++] = c[i];
      } else if(dist1 > 0){
        //if we get here we know for sure that both ends are on a different side of the plane
        //since dist1 > 0 we now that the first point is on the right side and the second is on the wrong side
        distratio = dist1/(dist1-dist2);
        //leave the first point untouched and insert it into the temp vertexes array
        theTempVertexes[j++] = c[i];
        Vector2D theTextureDistance = Texture2.distance(myTexture, c[i].myTextureCoordinate, c[ii].myTextureCoordinate);
        //insert a new vertex at the point where the line from the first vertex to the second intersects the plane
        theTempVertexes[j] = new Vertex(new Point3D(c[i].myPoint.x + (c[ii].myPoint.x - c[i].myPoint.x) * distratio,
            c[i].myPoint.y + (c[ii].myPoint.y - c[i].myPoint.y) * distratio,
            c[i].myPoint.z + (c[ii].myPoint.z - c[i].myPoint.z) * distratio),
            new Point2D(c[i].myTextureCoordinate.x + theTextureDistance.x * distratio,
                c[i].myTextureCoordinate.y + theTextureDistance.y * distratio),
                c[i].lightIntensity + (c[ii].lightIntensity - c[i].lightIntensity) * distratio);
        theTempVertexes[j].normal = new GVector( c[i].normal.x  + (c[ii].normal.x - c[i].normal.x) * distratio,
            c[i].normal.y + (c[ii].normal.y - c[i].normal.y) * distratio,
            c[i].normal.z + (c[ii].normal.z - c[i].normal.z) * distratio );
        j++;
        Vector2D.freeInstance(theTextureDistance);
      } else{
        //if we get here it means the first point is on the wrong side of the plane
        //the second is on the right side of theplane
        //insert a new Vertex there where the line from the first vertex to the second vertex intersects the plane
        //unlike the previous situation we do not add the point on the right side of the plane, it will be added in the next
        //iteration
        Vector2D theTextureDistance = Texture2.distance(myTexture, c[ii].myTextureCoordinate, c[i].myTextureCoordinate);
        distratio = dist2/(dist2-dist1);
        theTempVertexes[j] = new Vertex(new Point3D(c[ii].myPoint.x + (c[i].myPoint.x - c[ii].myPoint.x) * distratio,
            c[ii].myPoint.y + (c[i].myPoint.y - c[ii].myPoint.y) * distratio,
            c[ii].myPoint.z + (c[i].myPoint.z - c[ii].myPoint.z) * distratio),
            new Point2D(c[ii].myTextureCoordinate.x + theTextureDistance.x * distratio,
                c[ii].myTextureCoordinate.y + theTextureDistance.y * distratio),
                c[ii].lightIntensity + (c[i].lightIntensity - c[ii].lightIntensity) * distratio);
        theTempVertexes[j].normal = new GVector( c[ii].normal.x  + (c[i].normal.x - c[ii].normal.x) * distratio,
            c[ii].normal.y + (c[i].normal.y - c[ii].normal.y) * distratio,
            c[ii].normal.z + (c[i].normal.z - c[ii].normal.z) * distratio );
        j++;
        Vector2D.freeInstance(theTextureDistance);
      }							
    }
    c = theTempVertexes;
    myCamSize = j;

  }


  public void clip2Frustrum(Frustrum aFrustrum) throws PolygonException{
    for(int i=0;i<aFrustrum.myPlanes.length;i++){
      clip2Plane(aFrustrum.myPlanes[i]);
    }
    visible = !(c.length == 0);
  }

  public String toString(){
    StringBuffer theBuffer = new StringBuffer();
    theBuffer.append("<Polygon:\n");
    theBuffer.append(" centerpoint: " + myCenterPoint);
    theBuffer.append(" camSize: " + myCamSize);
    for(int i=0;i<myCamSize;i++){
      theBuffer.append(i + ": ");
      if(c[i] != null) theBuffer.append(c[i].toString());
      theBuffer.append("\n");
    }
    theBuffer.append(" normalVector: " + myNormalVector.toString());
    theBuffer.append("/>");
    return theBuffer.toString();
  }


  public void setColor(Color aColor) {
    color = aColor;
    createTexture();
  }

  public boolean containsVertex(Vertex aVertex){
    for(int i=0;i<w.length;i++){
      if(w[i].equals(aVertex)) return true;
    }
    return false;
  }

  public Color getColor() {
    return color;
  }


  public void setTexture(TextureImage aTextureImage,  boolean isSphericalTexture){
    myTextureImage = aTextureImage;
    this.isSphericalTexture = isSphericalTexture;
    myTexture = null;
    createTexture();
  }

  public void setTexture(String aTexture){
    setTexture(aTexture, true, false);
  }

  public void setTexture(String aTexture, boolean isTransparent, boolean isSphericalTexture){
    myTextureName = aTexture;
    isTransparentTexture = isTransparent;
    this.isSphericalTexture = isSphericalTexture;
    myTexture = null;
    createTexture();
  }

  public void setTexture(String aTexture, String aBumpMap, boolean isTransparent, boolean isSphericalTexture){
    myTextureName = aTexture;
    myBumpMap = aBumpMap;
    isTransparentTexture = isTransparent;
    this.isSphericalTexture = isSphericalTexture;
    myTexture = null;
    createTexture();
  }

  public Texture2 getTexture(){
    return myTexture;
  }
  
  public Point3D getCenterPoint(){
    return myCenterPoint;
  }

  public Point3D getCamCenterPoint() {
    return myCamCenterPoint;
  }

  public void setTexture(Texture2 anTexture) {
    myTexture = anTexture;
    calculateTexturePoints();
  }
  
  public iPixelShader[] getPixelShaders() {
    return myPixelShaders;
  }

  public void setPixelShaders(iPixelShader[] anPixelShaders) {
    myPixelShaders = anPixelShaders;
  }
  
  public List<Polygon> triangulate(){
    List<Polygon> thePolygons = new ArrayList<Polygon>();
    if(w.length > 3){
      //this is not a triangle, make new triangles from the points of this polygon
      for(int i=1;i<w.length-1;i++){
        thePolygons.add(new Polygon(w[0], w[i], w[i+1]));
      }
    } else {
      //it is a triangle
      //calculate the longest side of the triangle
      int theSideIndex = 0;
      float theSideLength = 0;
      for(int i=0;i<w.length;i++){
        GVector theVector = new GVector(w[i].myPoint, w[(i+1) % w.length].myPoint);
        float theLength = theVector.length();
        if(theLength > theSideLength){
          theSideIndex = i;
          theSideLength = theLength; 
        }
      }

      if(theSideLength > TRIANGULATION_THRESHOLD){
        //only triangulate when the longest side of the triangle is greater then the triangulation threshold
        //calculate the point half way this side
        Point3D thePoint = w[theSideIndex].myPoint.addition(w[(theSideIndex+1) % w.length].myPoint).division(2);
        Vertex theNewVertex = new Vertex(thePoint);
        theNewVertex.normal = w[theSideIndex].normal.addition(w[(theSideIndex+1) % w.length].normal).division(2);
        thePolygons.add(new Polygon(w[theSideIndex], theNewVertex , w[(theSideIndex + 2) % w.length]));
        thePolygons.add(new Polygon(theNewVertex, w[(theSideIndex + 1) % w.length], w[(theSideIndex + 2) % w.length]));
      } else {
        thePolygons.add(this);
      }
    }

    for(Polygon thePolygon : thePolygons){
      thePolygon.setTexture(myTexture);
      //      thePolygon.done();
      thePolygon.doubleSided = doubleSided;
    }

    return thePolygons;
  }
}