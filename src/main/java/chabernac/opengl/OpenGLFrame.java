package chabernac.opengl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import chabernac.control.KeyCommandListener;
import chabernac.control.KeyMap;
import chabernac.control.KeyMapContainer;
import chabernac.space.Camera;
import chabernac.space.Command3dFactory;
import chabernac.space.Graphics3DPipeline;
import chabernac.space.RotationManager;
import chabernac.space.ScreenFrustrum;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.geom.Shape;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.iVertexShader;

public class OpenGLFrame extends JFrame implements GLEventListener {
  private static final long serialVersionUID = 7282548388709481461L;
  private GL2 gl2;
  private Graphics3DPipeline pipeline;
  private World world;
  private Camera camera;
  private SynchronizedEventManagerAdapter myManager = new SynchronizedEventManagerAdapter();
  private long cycle = 0;

  public OpenGLFrame(World aWorld, Camera aCamera, Dimension aResolution) {
    this.world = aWorld;
    this.camera = aCamera;
    setSize(aResolution);
    init();
  }

  private void init() {
    setUpOpenGL();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setFocusable(true);
    requestFocus();
  }

  private KeyMapContainer buildKeyMapContainer() {
    KeyMapContainer keyMapContainer = new KeyMapContainer();

    /*
     * theContainer.addKeyMap(new KeyMap(KeyEvent.VK_D,
     * Command3dFactory.strafeDown(mySynchronizedTimer, aCamera, 1000000),2));
     * theContainer.addKeyMap(new KeyMap(KeyEvent.VK_E,
     * Command3dFactory.strafeUp(mySynchronizedTimer, aCamera, 1000000),2));
     * theContainer.addKeyMap(new KeyMap(KeyEvent.VK_S,
     * Command3dFactory.strafeLeft(mySynchronizedTimer, aCamera, 1000000),2));
     * theContainer.addKeyMap(new KeyMap(KeyEvent.VK_F,
     * Command3dFactory.strafeRight(mySynchronizedTimer, aCamera, 1000000),2));
     */

    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_D, Command3dFactory.strafeUp(myManager, camera, 5), 2));
    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_E, Command3dFactory.strafeDown(myManager, camera, 5), 2));
    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_S, Command3dFactory.strafeLeft(myManager, camera, 5), 2));
    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_F, Command3dFactory.strafeRight(myManager, camera, 5), 2));

    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_SPACE, Command3dFactory.forward(myManager, camera, 5), 2));
    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_ALT, Command3dFactory.backward(myManager, camera, 5), 2));

    keyMapContainer.addKeyMap(new KeyMap(new int[] { KeyEvent.VK_LEFT, KeyEvent.VK_NUMPAD4 }, Command3dFactory.left(myManager, camera, (float) Math.PI / 144)));
    keyMapContainer.addKeyMap(new KeyMap(new int[] { KeyEvent.VK_RIGHT, KeyEvent.VK_NUMPAD6 }, Command3dFactory.right(myManager, camera, (float) Math.PI / 144)));
    keyMapContainer.addKeyMap(new KeyMap(new int[] { KeyEvent.VK_DOWN, KeyEvent.VK_NUMPAD2 }, Command3dFactory.up(myManager, camera, (float) Math.PI / 144)));
    keyMapContainer.addKeyMap(new KeyMap(new int[] { KeyEvent.VK_UP, KeyEvent.VK_NUMPAD8 }, Command3dFactory.down(myManager, camera, (float) Math.PI / 144)));

    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD7, Command3dFactory.rollLeft(myManager, camera, (float) Math.PI / 144), 2));
    keyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD9, Command3dFactory.rollRight(myManager, camera, (float) Math.PI / 144), 2));

    // myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_R, new
    // ToggleRecordingCommand( myManager ),1));
    // myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_P, new PlayRecording(
    // myManager ),1));
    //
    return keyMapContainer;
  }

  private void setUpOpenGL() {
    GLProfile profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities capabilities = new GLCapabilities(profile);
    GLCanvas glcanvas = new GLCanvas(capabilities);
    Animator animator = new Animator(glcanvas);
    animator.setRunAsFastAsPossible(true);
    animator.start();
    glcanvas.addGLEventListener(this);
    glcanvas.addKeyListener(new KeyCommandListener(buildKeyMapContainer()));
    getContentPane().add(glcanvas);
  }

  private void setupGraphics3d() {
    Point3D theEyePoint = new Point3D(getWidth() / 2, getHeight() / 2, (getWidth() + getHeight()) / 2);
    ScreenFrustrum theFrustrum = new ScreenFrustrum(theEyePoint, new Dimension(getWidth() - 1, getHeight() - 1), 0.001F, 5000);
    pipeline = new Graphics3DPipeline(theFrustrum, theEyePoint, camera, world, new OpenGLAdapter(gl2, theFrustrum.getDepth()));
    pipeline.setVertexShaders(new iVertexShader[] { new GouroudShading((float) 0.3) });
    pipeline.setDimensions(getWidth(), getHeight());
    pipeline.setDrawRibs(false);
    pipeline.setDrawPlanes(true);
    pipeline.setDrawVertexNormals(false);
    pipeline.setDrawNormals(false);
    pipeline.setDrawFPS(true);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    this.gl2 = drawable.getGL().getGL2();
    gl2.glClearColor(0f, 0f, 0f, 1f);
    gl2.glEnable(GL.GL_DEPTH_TEST);
    setupGraphics3d();
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {

  }

  @Override
  public void display(GLAutoDrawable drawable) {
    cycle++;
    myManager.fireEvent(cycle);
    pipeline.drawWorld(cycle);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    pipeline.setDimensions(width, height);

  }

  public static void main(String[] args) {
    World world = new World();
    // Shape theShape = ShapeFactory.makeCube(new Point3D(200, 0, 400), 94, 94, 94);

    // Shape thePolygon = ShapeFactory.makeSinglePolygonShape(new Point3D(0,0,0),
    // 94, 94);
    // thePolygon.setTexture("guy");se
    //// thePolygon.setTexture("guy", "axa", false, false);
    // world.addShape(thePolygon);

    for (int i = 0; i <= 100; i++) {
      Shape theShape = ShapeFactory.makeCube(new Point3D(i * 300, i * 300, i * 300), 400, 800, 94);
      // theShape.setTexture("guy", "axa", false, false);s
      theShape.setTexture("metal006");
      world.addShape(theShape);
    }

    Shape theCosmos = ShapeFactory.makeSphere(new Point3D(400, 500, 1000), 10000, 50);
    theCosmos.setRoom(true);
    theCosmos.setTexture("starmap", false, true);
    theCosmos.done();
    world.addShape(theCosmos);

    Shape theEarth = ShapeFactory.makeSphere(new Point3D(400, 500, 1000), 800, 60);
    theEarth.setColor(Color.blue);
    theEarth.setRoom(false);
    theEarth.setTexture("world", false, true);
    // theCosmos.setTexture("stars", false, true);
    // theCosmos.setTexture("star_map_small", false, true);

    // theCosmos.setTexture("Threadplate0069_1_S", false, true);
    // theCosmos.setTexture("EarthMap_2500x1250", false, true);
    theEarth.done();
    world.addShape(theEarth);

    RotationManager theEarthRotationManager = new RotationManager(new Rotation(0, 0, -(float) Math.PI / 1800));
    theEarthRotationManager.setRotationCenter(theEarth);
    theEarthRotationManager.addTranslatable(theEarth);
    world.getTranslateManagerContainer().addTranslateManager(theEarthRotationManager);

    OpenGLFrame theFrame = new OpenGLFrame(world, new Camera(new Point3D(100f, 100f, 0f), new Rotation(0f, 0f, 0f), 1), new Dimension(800, 600));
    theFrame.setVisible(true);
  }
}
