package chabernac.space.shapes;

import org.junit.Test;

import chabernac.space.Camera;
import chabernac.space.geom.Point3D;

public class CubeTest {
	@Test
	public void testWorld2Cam() {
		Camera theCamera = new Camera();
		Cube theCube = new Cube(new Point3D(0, 0, 0), 20);

		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			theCube.world2Cam(theCamera);
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);

	}

}
