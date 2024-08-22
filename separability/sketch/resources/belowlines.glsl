// given an array of lines, renders each pixels either blue or green
// depending if the pixel lies below an even number of lines or not
// this is the natural two colouring for a line arrangement.
#ifdef GL_ES
precision highp float;
precision highp int;
#endif

#define PROCESSING_COLOR_SHADER

#define MAX_LINES 2*64

#define MIN(A,B) ((A) < (B) ? (A) : (B))

uniform int size; // the actual size of lines (we cannot pass a dynamically sized array)
uniform float lines[MAX_LINES]; // {m0, q0, m1, q1, ...}

vec3 red = vec3(1.0, 0.0, 0.0);
vec3 green = vec3(0.0, 1.0, 0.0);
vec3 blue = vec3(0.0, 0.0, 1.0);
vec3 purple = vec3(1.0, 0.0, 1.0);
vec3 black = vec3(0.0, 0.0, 0.0);

bool belowLine(vec2 lne, vec2 pos) {
    return pos.y < lne.x * pos.x + lne.y;
}

void main(void) {
    vec2 position = gl_FragCoord.xy;
    int belowCount = 0;
    for(int i = 0; i < MIN(size, MAX_LINES); i += 2) {
        vec2 line = vec2(lines[i], lines[i + 1]);
        if(belowLine(line, position)) {
            belowCount += 1;
        }
    }
    if(belowCount % 2 == 0) {
        gl_FragColor = vec4(green, 0.05);
    } else {
        gl_FragColor = vec4(red, 0.05);
    }
}
