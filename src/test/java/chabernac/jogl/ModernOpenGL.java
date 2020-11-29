package chabernac.jogl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import jogamp.graph.font.typecast.ot.table.Program;

public class ModernOpenGL implements GLEventListener {
	private interface Buffer {
		int VERTEX = 0;
		int ELEMENT = 1;
		int GLOBAL_MATRICES = 2;
		int MODEL_MATRIX = 3;
		int MAX = 4;
	}

	private float[] vertexData = {
			-1, -1, 1, 0, 0,
			+0, +2, 0, 0, 1,
			+1, -1, 0, 1, 0 };

	private short[] elementData = { 0, 2, 1 };
	private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);
	private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);

	@Override
	public void display(GLAutoDrawable drawable) {
		draw(drawable);
	}

	private void draw(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// method body
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = arg0.getGL().getGL4();
		gl.glGenBuffers(Buffer.MAX, bufferName);
		
		FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
		ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL.GL_STATIC_DRAW);
//		gl.glBufferStorage(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
//		gl.glBufferStorage(GL.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, 0);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		ShaderProgram program = new ShaderProgram()
				.a
//		 Program program = new Program(gl, getClass(), "shaders/gl4", "hello-triangle.vert", "hello-triangle.frag");
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// method body
	}

	public static void main(String[] args) {
		// getting the capabilities object of GL2 profile
		final GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		// The canvas
		GLWindow window = GLWindow.create(capabilities);
		Animator animator = new Animator(window);
		animator.start();
		ModernOpenGL l = new ModernOpenGL();
		window.addGLEventListener(l);
		window.setSize(400, 400);
		window.setVisible(true);
	}
}
