#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/***********************common function*************************/
#define  LOG_TAG    "libes2test.so"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

const static GLfloat PI = 3.1415f;

static void printGLString(const char *name, GLenum s) {
    const char *v = (const char *) glGetString(s);
    LOGI("GL %s = %s\n", name, v);
}

static void checkGlError(const char* op) {
    for (GLint error = glGetError(); error; error
            = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}

//data
const GLfloat gVertices[] = { 0.75f, 0.75f, 0.0f, 1.0f,
		0.75f, -0.75f, 0.0f, 1.0f,
		-0.75f, -0.75f, 0.0f, 1.0f, };
const GLfloat gTexCoords[] = { 0, 1, 1, 1, 0, 0, 1, 0};

GLuint textureID;

GLuint positionBufferObject;
GLuint vao;

static void InitializeVertexBuffer()
{
	glGenBuffers(1, &positionBufferObject);

	glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	glBufferData(GL_ARRAY_BUFFER, sizeof(gVertices), gVertices, GL_STATIC_DRAW);
	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

static const char gVertexShader[] =
		"attribute vec4 a_Position;\n"
		"void main()\n"
		"{\n"
		"   gl_Position = position;\n"
		"}\n";

static const char gFragmentShader[] =
	    "precision mediump float;\n"
		"void main()\n"
		"{\n"
		"   gl_FragColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);\n"
		"}\n";

GLuint loadShader(GLenum shaderType, const char* pSource) {
    GLuint shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char* buf = (char*) malloc(infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("Could not compile shader %d:\n%s\n",
                            shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    if (!vertexShader) {
        return 0;
    }

    GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
    if (!pixelShader) {
        return 0;
    }

    GLuint program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char* buf = (char*) malloc(bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOGE("Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

GLuint gProgram;
GLuint gPositionHandle;
GLuint gTexCoordsHandle;
GLuint gTexHandle;

static void loadTexture(JNIEnv * env, jobject bitmap)
{
	AndroidBitmapInfo  info;
	void*  pixels;
	int format;
	int ret;
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
	        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
	        return;
	    }

	    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
	        LOGI("Bitmap format is  RGBA_8888 !");
	        format = GL_RGBA;
	    } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {
	        LOGI("Bitmap format is  RGB_565 !");
	        format = GL_RGB;
	    }

	    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
	        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	    }

	    glGenTextures(1, &textureID);
	    glBindTexture(GL_TEXTURE_2D, textureID);
        // Set filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, format, info.width, info.height, 0, format, GL_UNSIGNED_BYTE, pixels);

	    AndroidBitmap_unlockPixels(env, bitmap);
}

static void init(JNIEnv * env, jobject bitmap)
{
		InitializeVertexBuffer();

	    gProgram = createProgram(gVertexShader, gFragmentShader);
	    if (!gProgram) {
	        LOGE("Could not create program.");
	        return ;
	    }
	    gPositionHandle = glGetAttribLocation(gProgram, "a_Position");
	    checkGlError("glGetAttribLocation");
	    LOGI("glGetAttribLocation(\"a_Position\") = %d\n",
	            gPositionHandle);
/*
	    gTexCoordsHandle = glGetAttribLocation(gProgram, "a_TexCoords");
	    checkGlError("glGetAttribLocation");
	    LOGI("glGetAttribLocation(\"a_TexCoords\") = %d\n",
	    		gTexCoordsHandle);
	    gTexHandle = glGetUniformLocation(gProgram, "u_Texture");
	    checkGlError("glGetUniformLocation");
	    LOGI("glGetUniformLocation(\"u_Texture\") = %d\n",
	    		gTexHandle);
*/
}

static void resize(int w, int h)
{
	   if (h==0)								// 防止被零除
	  {
			  h=1;							// 将Height设为1
	  }

	  glViewport(0, 0, w, h);					// 重置当前的视口

}

static void update()
{
	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	glClear(GL_COLOR_BUFFER_BIT );

    glUseProgram(gProgram);
    checkGlError("glUseProgram");
    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    checkGlError("glBindBuffer");
	glEnableVertexAttribArray(0);
	checkGlError("glEnableVertexAttribArray");
	glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 0, 0);
	checkGlError("glVertexAttribPointer");
	glDrawArrays(GL_TRIANGLES, 0, 3);
	checkGlError("glDrawArrays");

	glDisableVertexAttribArray(0);
	glUseProgram(0);
}

extern "C" {
    JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_resize(JNIEnv * env, jobject obj,  jint width, jint height);
    JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_draw(JNIEnv * env, jobject obj);
    JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_init(JNIEnv * env, jobject obj, jobject bitmap);
};

JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_resize(JNIEnv * env, jobject obj,  jint width, jint height)
{
	resize(width, height);
}

JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_draw(JNIEnv * env, jobject obj)
{
	update();
}

JNIEXPORT void JNICALL Java_com_fyj_demo_es_jni_Test1Renderer_init(JNIEnv * env, jobject obj, jobject bitmap)
{
	init(env, bitmap);
}
