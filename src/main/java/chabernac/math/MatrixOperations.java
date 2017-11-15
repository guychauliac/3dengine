/*
 * Created on 9-jan-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.math;

import chabernac.space.CoordinateSystem;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

/**
 * @author Administrator
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MatrixOperations {

	public static Matrix buildRotationMatrix(Rotation aRotation) throws MatrixException {
		Matrix theXRotation = new Matrix(4, 4);
		theXRotation.setValueAt(0, 0, 1);
		theXRotation.setValueAt(1, 1, aRotation.myPitchCos);
		theXRotation.setValueAt(1, 2, -aRotation.myPitchSin);
		theXRotation.setValueAt(2, 1, aRotation.myPitchSin);
		theXRotation.setValueAt(2, 2, aRotation.myPitchCos);
		theXRotation.setValueAt(3, 3, 1);

		Matrix theYRotation = new Matrix(4, 4);
		theYRotation.setValueAt(0, 0, aRotation.myYawCos);
		theYRotation.setValueAt(0, 2, -aRotation.myYawSin);
		theYRotation.setValueAt(1, 1, 1);
		theYRotation.setValueAt(2, 0, aRotation.myYawSin);
		theYRotation.setValueAt(2, 2, aRotation.myYawCos);
		theYRotation.setValueAt(3, 3, 1);

		Matrix theZRotation = new Matrix(4, 4);
		theZRotation.setValueAt(0, 0, aRotation.myRollCos);
		theZRotation.setValueAt(0, 1, aRotation.myRollSin);
		theZRotation.setValueAt(1, 0, -aRotation.myRollSin);
		theZRotation.setValueAt(1, 1, aRotation.myRollCos);
		theZRotation.setValueAt(2, 2, 1);
		theZRotation.setValueAt(3, 3, 1);

		return (theXRotation.multiply(theYRotation)).multiply(theZRotation);
	}

	public static Matrix buildTranslationMatrix(Point3D aPoint) throws MatrixException {
		return buildTranslationMatrix(new GVector(aPoint).inv());
	}

	/**
	 * <pre>
	 * [ 1         ]
	 * [    1      ]
	 * [       1   ]
	 * [ x  y  z  1]
	 * </pre>
	 */
	public static Matrix buildTranslationMatrix(GVector aVector) throws MatrixException {
		Matrix theMatrix = new Matrix(4, 4);
		theMatrix.setValueAt(0, 0, 1);
		theMatrix.setValueAt(1, 1, 1);
		theMatrix.setValueAt(2, 2, 1);
		theMatrix.setValueAt(3, 3, 1);
		theMatrix.setValueAt(3, 0, aVector.x);
		theMatrix.setValueAt(3, 1, aVector.y);
		theMatrix.setValueAt(3, 2, aVector.z);
		return theMatrix;
	}

	public static Matrix buildScalingMatrix(float xScaling, float yScaling, float zScaling) throws MatrixException {
		Matrix theMatrix = new Matrix(4, 4);
		theMatrix.setValueAt(0, 0, xScaling);
		theMatrix.setValueAt(1, 1, yScaling);
		theMatrix.setValueAt(2, 2, zScaling);
		theMatrix.setValueAt(3, 3, 1);
		return theMatrix;
	}

	/**
	 * <pre>
	 * [x y z 1 ] x [xunit.x yunit.x zunit.z 0] 
	 *              [xunit.y yunit.y zunit.y 0]  
	 *              [xunit.z yunit.z zunit.z 0] 
	 *              [0       0     0         1]
	 * </pre>
	 */
	public static Matrix buildTransformationMatrix(CoordinateSystem aCoordinadateSystem) throws MatrixException {
		Matrix theTransformationMatrix = new Matrix(4, 4);
		GVector theXUnit = aCoordinadateSystem.getXUnit().norm();
		theTransformationMatrix.setValueAt(0, 0, theXUnit.x);
		theTransformationMatrix.setValueAt(1, 0, theXUnit.y);
		theTransformationMatrix.setValueAt(2, 0, theXUnit.z);
		theTransformationMatrix.setValueAt(3, 0, 0);

		GVector theYUnit = aCoordinadateSystem.getYUnit().norm();
		theTransformationMatrix.setValueAt(0, 1, theYUnit.x);
		theTransformationMatrix.setValueAt(1, 1, theYUnit.y);
		theTransformationMatrix.setValueAt(2, 1, theYUnit.z);
		theTransformationMatrix.setValueAt(3, 1, 0);

		GVector theZUnit = aCoordinadateSystem.getZUnit().norm();
		theTransformationMatrix.setValueAt(0, 2, theZUnit.x);
		theTransformationMatrix.setValueAt(1, 2, theZUnit.y);
		theTransformationMatrix.setValueAt(2, 2, theZUnit.z);
		theTransformationMatrix.setValueAt(3, 2, 0);

		theTransformationMatrix.setValueAt(0, 3, 0);
		theTransformationMatrix.setValueAt(1, 3, 0);
		theTransformationMatrix.setValueAt(2, 3, 0);
		theTransformationMatrix.setValueAt(3, 3, 1);

		Matrix theTranslationMatrix = buildTranslationMatrix(aCoordinadateSystem.getOrigin());
		Matrix theScalingMatrix = buildScalingMatrix(1 / aCoordinadateSystem.getXUnit().length(),
				1 / aCoordinadateSystem.getYUnit().length(), 1 / aCoordinadateSystem.getZUnit().length());

		return theTranslationMatrix.multiply(theTransformationMatrix).multiply(theScalingMatrix);
	}

	public static Matrix buildMatrix(Point3D aPoint) {
		return new Matrix(1, 4, new float[] { aPoint.x, aPoint.y, aPoint.z, 1 });
	}

	public static Point3D buildPoint3d(Matrix aMatrix) {
		return new Point3D(aMatrix.myMatrix[0], aMatrix.myMatrix[1], aMatrix.myMatrix[2]);
	}

	public static Matrix buildMatrix(GVector aVector) {
		return new Matrix(1, 4, new float[] { aVector.x, aVector.y, aVector.z, 1 });
	}

	public static Matrix buildIdentityMatrix() {
		Matrix theMatrix = new Matrix(4, 4);
		for (int row = 0; row < theMatrix.getRows(); row++) {
			for (int column = 0; column < theMatrix.getColumns(); column++) {
				if (row == column) {
					theMatrix.setValueAt(row, column, 1);
				} else {
					theMatrix.setValueAt(row, column, 0);
				}
			}
		}
		return theMatrix;
	}

	public static GVector buildGVector(Matrix aMatrix) {
		float[] theSource = aMatrix.getSource();
		return new GVector(theSource[0], theSource[1], theSource[2]);
	}
}
