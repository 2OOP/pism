package org.toop.frontend.platform.graphics.opengl;

import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.toop.eventbus.*;
import org.toop.eventbus.events.Events;
import org.toop.frontend.graphics.Renderer;
import org.toop.frontend.graphics.Shader;

public class OpenglRenderer extends Renderer {
    private Shader shader;
    private int vao;

    public OpenglRenderer() {
        GL.createCapabilities();
        GL45.glClearColor(0.65f, 0.9f, 0.65f, 1f);

        GlobalEventBus.subscribeAndRegister(
                Events.WindowEvents.OnResize.class,
                event -> {
                    GL45.glViewport(0, 0, event.size().width(), event.size().height());
                });

        logger.info("Opengl renderer setup.");

        // Form here on, everything is temporary
        float vertices[] = {
            -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 1.0f, 1.0f, 0.0f,
        };

        int indicies[] = {
            0, 1, 2,
            2, 3, 0,
        };

        vao = GL45.glCreateVertexArrays();
        GL45.glBindVertexArray(vao);

        int vbo = GL45.glCreateBuffers();
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, vbo);
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, vertices, GL45.GL_STATIC_DRAW);

        GL45.glVertexAttribPointer(0, 2, GL45.GL_FLOAT, false, 5 * 4, 0);
        GL45.glVertexAttribPointer(1, 3, GL45.GL_FLOAT, false, 5 * 4, 2 * 4);

        GL45.glEnableVertexAttribArray(0);
        GL45.glEnableVertexAttribArray(1);

        int ib = GL45.glCreateBuffers();
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, ib);
        GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, indicies, GL45.GL_STATIC_DRAW);

        shader =
                Shader.create(
                        "src/main/resources/shaders/gui_vertex.glsl",
                        "src/main/resources/shaders/gui_fragment.glsl");
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void clear() {
        GL45.glClear(GL45.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void render() {
        // temporary
        //	shader.start();
        GL45.glBindVertexArray(vao);
        GL45.glDrawElements(GL45.GL_TRIANGLES, 6, GL45.GL_UNSIGNED_INT, MemoryUtil.NULL);
    }
}
