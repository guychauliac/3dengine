package chabernac.opengl;

import java.awt.Color;
import java.awt.Font;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.WeakHashMap;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import chabernac.space.buffer.DrawingRectangleContainer;
import chabernac.space.buffer.iPixelListener;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Polygon;
import chabernac.space.geom.Polygon2D;
import chabernac.space.geom.Vertex2D;
import chabernac.space.geom.VertexLine2D;
import chabernac.space.shading.iPixelShader;
import chabernac.space.texture.TextureImage;
import chabernac.utils.i3DGraphics;

public class OpenGLAdapter implements i3DGraphics {
  private final GL gl;
  private final GL2 gl2;
  private int halfWidht;
  private int halfHeight;
  private float frustrumDepth;

  private final WeakHashMap<TextureImage, Texture> textureData = new WeakHashMap<>();

  public OpenGLAdapter(GL gl, float aFrustrumDepth) {
    super();
    this.gl = gl;
    this.gl2 = gl.getGL2();
    this.frustrumDepth = aFrustrumDepth;
  }

  @Override
  public void drawPolygon(Polygon2D aPolygon, Polygon anOrigPolygon) {
    Texture theOpenGLTexture = getTextureData(aPolygon.getTexture().getTextureImage());
    if (theOpenGLTexture != null) {
      gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
      gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
      theOpenGLTexture.bind(gl);
    }
    gl2.glBegin(GL2.GL_POLYGON);
    for (Vertex2D vertex : aPolygon.getVertexes()) {
      createVertex(vertex);
    }
    gl2.glEnd();
  }

  private Texture getTextureData(TextureImage aTextureImage) {
    if (aTextureImage == null) {
      return null;
    }

    if (!textureData.containsKey(aTextureImage)) {
      TextureData data = new TextureData(GLProfile.get(GLProfile.GL2), GL.GL_RGBA, aTextureImage.width,
          aTextureImage.height, 0, GL.GL_RGBA, GL2GL3.GL_UNSIGNED_INT_8_8_8_8, false, false, false,
          IntBuffer.wrap(aTextureImage.colors), null);
      Texture theOpgenGLTexture = TextureIO.newTexture(data);
      theOpgenGLTexture.bind(gl);
      theOpgenGLTexture.enable(gl);
      textureData.put(aTextureImage, theOpgenGLTexture);
    }
    return textureData.get(aTextureImage);
  }

  @Override
  public void drawLine(VertexLine2D aLine) {
    gl2.glBegin(GL2.GL_LINES);// static field
    gl2.glColor3f(0f, 1f, 0f);
    createVertex(aLine.getStart());
    createVertex(aLine.getEnd());
    gl2.glEnd();
  }

  private void createVertex(Vertex2D point) {
    // System.out.println("point: " + point.getPoint().toString());
    float x = point.getPoint().x / halfWidht - 1;
    float y = point.getPoint().y / halfHeight - 1;
    if (point.getTexturePoint() != null) {
      // System.out.println("texture: " + point.getTexturePoint().toString());
      // gl2.glTexCoord2d(point.getTexturePoint().x / 94f, point.getTexturePoint().y /
      // 94f);
      gl2.glTexCoord2d(point.getTexturePoint().x, point.getTexturePoint().y);
      // gl2.glTexCoord2d(0, 0);
    }
    gl2.glVertex3f(x, y, point.getDepth() / frustrumDepth);
    // gl2.glTexCoord2d(10000, 100.11);
  }

  @Override
  public void drawText(Point2D aPoint, String aText, Color aColor) {
    TextRenderer textRenderer = new TextRenderer(new Font("Verdana", Font.BOLD, 12));
    textRenderer.beginRendering(halfWidht * 2, halfHeight * 2);
    textRenderer.setColor(aColor);
    textRenderer.setSmoothing(true);
    textRenderer.draw(aText, (int)aPoint.x, (int)aPoint.y);
    textRenderer.endRendering();
  }

  @Override
  public void drawRect(int x, int y, int width, int height) {
    // TODO Auto-generated method stub

  }

  @Override
  public void fillOval(int x, int y, int width, int height) {
    // TODO Auto-generated method stub

  }

  @Override
  public void drawOval(int x, int y, int width, int height) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setColor(Color c) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setBackGroundColor(int aBackGroundColor) {
    // TODO Auto-generated method stub

  }

  @Override
  public void clear() {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public void cycleDone() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setPixelListener(iPixelListener anPixelListener) {
    // TODO Auto-generated method stub

  }

  @Override
  public Collection<DrawingRectangleContainer> getDrawingRectangles() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDimensions(int aWidth, int aHeight) {
    halfWidht = aWidth / 2;
    halfHeight = aHeight / 2;
  }

  @Override
  public void setUsePartialClearing(boolean aUsePartialClearing) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setPixelShaders(iPixelShader[] aPixelShaders) {
    // TODO Auto-generated method stub

  }

  @Override
  public void drawImage(long aCycle) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setSingleFullRepaint(boolean aB) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setUseClipping(boolean aB) {
    // TODO Auto-generated method stub

  }

}
