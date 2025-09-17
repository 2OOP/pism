package org.toop;

import org.toop.core.*;
import org.toop.eventbus.*;
import org.toop.graphics.*;
import org.toop.server.backend.ServerManager;
import org.toop.server.frontend.ConnectionManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.ExecutionException;

public class Main {
	private static boolean running = false;

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		registerEvents();

		Window window = Window.setup(Window.API.GLFW, "Test", new Window.Size(1280, 720));
		Renderer renderer = Renderer.setup(Renderer.API.OPENGL);
        initSystems();
        Logging.disableLogs();

		Shader shader = Shader.create(
			"src/main/resources/shaders/gui_vertex.glsl",
			"src/main/resources/shaders/gui_fragment.glsl");

		running = window != null && renderer != null && shader != null;
		ConsoleGui console = new ConsoleGui();

		while (running) {
			window.update();
			renderer.clear();

			shader.start();
			renderer.render();
		}
		console.print();
    }

		if (shader != null) shader.cleanup();
		if (renderer != null) renderer.cleanup();
		if (window != null) window.cleanup();

         */
        //JFrameWindow window = new JFrameWindow();
        GameSelectorWindow gameSelectorWindow = new GameSelectorWindow();

	}

	private static void registerEvents() {
		GlobalEventBus.subscribeAndRegister(Events.WindowEvents.OnQuitRequested.class, event -> {
			quit();
		});

		GlobalEventBus.subscribeAndRegister(Events.WindowEvents.OnMouseMove.class, event -> {
		});
	}
    public static void initSystems() {
        new ServerManager();
        new ConnectionManager();
    }

	private static void quit() {
		running = false;
	}

	public static boolean isRunning() {
		return running;
	}

	public static void setRunning(boolean running) {
		Main.running = running;
	}
}
