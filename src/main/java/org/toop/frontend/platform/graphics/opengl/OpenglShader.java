package org.toop.frontend.platform.graphics.opengl;

import org.toop.core.*;
import org.toop.frontend.graphics.Shader;

import org.lwjgl.opengl.*;

public class OpenglShader extends Shader {
	private int programID;

	public OpenglShader(String vertexPath, String fragmentPath) {
		FileSystem.File vertexSource = FileSystem.read(vertexPath);
		FileSystem.File fragmentSource = FileSystem.read(fragmentPath);

		if (vertexSource == null || fragmentPath == null) {
			return;
		}

		programID = GL45.glCreateProgram();

		int vertexShader = GL45.glCreateShader(GL45.GL_VERTEX_SHADER);
		int fragmentShader = GL45.glCreateShader(GL45.GL_FRAGMENT_SHADER);

		GL45.glShaderSource(vertexShader, vertexSource.buffer());
		GL45.glShaderSource(fragmentShader, fragmentSource.buffer());

		GL45.glCompileShader(vertexShader);
		GL45.glCompileShader(fragmentShader);

		GL45.glAttachShader(programID, vertexShader);
		GL45.glAttachShader(programID, fragmentShader);

		GL45.glLinkProgram(programID);
		GL45.glValidateProgram(programID);

		GL45.glDetachShader(programID, vertexShader);
		GL45.glDetachShader(programID, fragmentShader);

		GL45.glDeleteShader(vertexShader);
		GL45.glDeleteShader(fragmentShader);
	}

	@Override
	public void cleanup() {
		stop();
		GL45.glDeleteProgram(programID);
	}

	@Override
	public void start() {
		GL45.glUseProgram(programID);
	}

	@Override
	public void stop() {
		GL45.glUseProgram(0);
	}
}
