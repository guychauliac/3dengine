/*
 * Created on 14-dec-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import chabernac.control.ISynchronizedEventManager;
import chabernac.control.KeyCommand;
import chabernac.math.MatrixException;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Command3dFactory {
	public static KeyCommand strafeLeft(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
		return new CameraMoveCommand("Strafe left", aManager, aCamera, new Camera(new Point3D(-aSpeed, 0, 0), new Rotation(0,0,0), 1F));
	}
	
	public static KeyCommand strafeRight(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Strafe right", aManager, aCamera, new Camera(new Point3D(aSpeed, 0, 0), new Rotation(0,0,0), 1F));
	}
	
	public static KeyCommand forward(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Forward", aManager, aCamera, new Camera(new Point3D(0, 0, aSpeed), new Rotation(0,0,0), 1F));
	}
	
	public static KeyCommand backward(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Backwards", aManager, aCamera, new Camera(new Point3D(0, 0, -aSpeed), new Rotation(0,0,0), 1F));
	}
	
	public static KeyCommand strafeUp(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Strafe up", aManager, aCamera, new Camera(new Point3D(0, aSpeed, 0), new Rotation(0,0,0), 1F));
	}
	
	public static KeyCommand strafeDown(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Strafe down", aManager, aCamera, new Camera(new Point3D(0, -aSpeed, 0), new Rotation(0,0,0), 1F));
	}	
	
	public static KeyCommand left(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Left", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(0,0,+aSpeed), 1F));
	}
	
	public static KeyCommand right(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Right", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(0,0,-aSpeed), 1F));
	}
	
	public static KeyCommand up(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Up", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(0,aSpeed,0), 1F));
	}
	
	public static KeyCommand down(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Down", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(0,-aSpeed,0), 1F));
	}
	
	public static KeyCommand rollLeft(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Roll left", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(-aSpeed,0,0), 1F));
	}
		
	public static KeyCommand rollRight(ISynchronizedEventManager aManager, Camera aCamera, float aSpeed) throws MatrixException{
			return new CameraMoveCommand("Roll right", aManager, aCamera, new Camera(new Point3D(0, 0, 0), new Rotation(aSpeed,0,0), 1F));
	}
}
