#version 450 core

layout(location = 0) in vec2 in_position;
layout(location = 1) in vec3 in_color;

out vec3 pass_color;

void main(void) {
	gl_Position = vec4(in_position, 0.0f, 1.0f);
	pass_color = in_color;
}
