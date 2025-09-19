package org.toop.frontend.platform.core.glfw;

import org.toop.core.*;
import org.toop.eventbus.*;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

public class GlfwWindow extends Window {
	private long window;

	public GlfwWindow(String title, Size size) {
		if (!GLFW.glfwInit()) {
			logger.fatal("Failed to initialize glfw");
			return;
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		int width = size.width();
		int height = size.height();

		if (width <= 0 || height <= 0 || width > videoMode.width() || height > videoMode.height()) {
			width = videoMode.width();
			height = videoMode.height();

			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
		}

		long window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL) {
			GLFW.glfwTerminate();

			logger.fatal("Failed to create glfw window");
			return;
		}

		int[] widthBuffer = new int[1];
		int[] heightBuffer = new int[1];
		GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer);

		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(1);

		GLFW.glfwSetWindowCloseCallback(window, (lwindow) -> {
			GlobalEventBus.post(new Events.WindowEvents.OnQuitRequested());
		});

		GLFW.glfwSetFramebufferSizeCallback(window, (lwindow, lwidth, lheight) -> {
			GlobalEventBus.post(new Events.WindowEvents.OnResize(new Size(lwidth, lheight)));
		});

		GLFW.glfwSetCursorPosCallback(window, (lwindow, lx, ly) -> {
			GlobalEventBus.post(new Events.WindowEvents.OnMouseMove((int)lx, (int)ly));
		});

		GLFW.glfwSetMouseButtonCallback(window, (lwindow, lbutton, laction, lmods) -> {
			switch (laction) {
				case GLFW.GLFW_PRESS:
					GlobalEventBus.post(new Events.WindowEvents.OnMouseClick(lbutton));
					break;

				case GLFW.GLFW_RELEASE:
					GlobalEventBus.post(new Events.WindowEvents.OnMouseRelease(lbutton));
					break;

				default: break;
			}
		});

		this.window = window;
		GLFW.glfwShowWindow(window);

		logger.info("Glfw window setup. Title: {}. Width: {}. Height: {}.", title, size.width(), size.height());
	}

	@Override
	public void cleanup() {
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();

		super.cleanup();
	}

	@Override
	public void update() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}
}
