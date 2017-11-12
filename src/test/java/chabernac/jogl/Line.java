package chabernac.jogl;

import java.nio.IntBuffer;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Line implements GLEventListener {
	@Override
	public void display(GLAutoDrawable drawable) {
		draw(drawable);
		draw(drawable);
	}

	private void draw(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		TextureData data = new TextureData(GLProfile.get(GLProfile.GL2), GL.GL_RGBA, 1, 1, 0, GL.GL_RGBA,
				GL2GL3.GL_UNSIGNED_INT_8_8_8_8, false, false, false, IntBuffer.wrap(new int[] { 0x0000FFAA }), null); // BGR
		Texture theTexture = TextureIO.newTexture(data);
		theTexture.bind(gl);
		theTexture.enable(gl);
		gl.glBegin(GL2.GL_POLYGON);// static field
		// gl.glColor3f(1f, 0f, 0f);

		gl.glTexCoord2d(0, 0);
		gl.glVertex3f(0.50f, -0.50f, 0);

		gl.glTexCoord2d(0, 0);
		gl.glVertex3f(-0.50f, 0.50f, 0);
		// gl.glColor3f(0f, 0f, 1f);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3f(1f, 1f, 0);
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// method body
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// method body
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// method body
	}

	public static void main(String[] args) {
		// getting the capabilities object of GL2 profile
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		// The canvas
		final GLCanvas glcanvas = new GLCanvas(capabilities);
		Line l = new Line();
		glcanvas.addGLEventListener(l);
		glcanvas.setSize(400, 400);
		// creating frame
		final JFrame frame = new JFrame("straight Line");
		// adding canvas to frame
		frame.getContentPane().add(glcanvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.setVisible(true);
	}// end of main
}// end of classimport javax.media.opengl.GL2;
