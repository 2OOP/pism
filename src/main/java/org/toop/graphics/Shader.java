package org.toop.graphics;

import org.toop.platform.graphics.opengl.*;

public abstract class Shader {
	public static Shader create(String vertexPath, String fragmentPath) {
		Shader shader = null;

		switch (Renderer.getApi()) {
			case OPENGL:
				shader = new OpenglShader(vertexPath, fragmentPath);
				break;

			case NONE:
			default:
				break;
		}

		return shader;
	}

	public abstract void cleanup();

	public abstract void start();
	public abstract void stop();
}
