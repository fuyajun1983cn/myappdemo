#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#define USING_ES11

#ifdef USING_ES11
#include <GLES/gl.h>
#include <GLES/glext.h>
#else
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#endif

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/***********************common function*************************/
#define  LOG_TAG    "libtexture_test.so"
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
const GLfloat gVertices[] = { -1, -1, 1, -1, -1, 1, 1, 1 };
const GLfloat gTexCoords[] = { 0, 1, 1, 1, 0, 0, 1, 0};

GLuint textureID;

#ifndef USING_ES11
static const char gVertexShader[] =
    "attribute vec4 a_Position;\n"
	"attribute vec2 a_TexCoords; \n"
	"varying vec2 v_TexCoords; \n"
    "void main() {\n"
    "  gl_Position = a_Position;\n"
	" v_TexCoords = a_TexCoords; \n"
    "}\n";

static const char gFragmentShader[] =
    "precision mediump float;\n"
    "uniform sampler2D u_Texture; \n"
	"varying vec2 v_TexCoords; \n"
    "void main() {\n"
    "  gl_FragColor = texture2D(u_Texture, v_TexCoords);\n"
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

#endif

static void init(JNIEnv * env, jobject bitmap)
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

#ifdef USING_ES11
	    glShadeModel(GL_SMOOTH);						// 启用阴影平滑
	    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);					// 黑色背景
	    glClearDepthf(1.0f);							// 设置深度缓存
	    glEnable(GL_DEPTH_TEST);						// 启用深度测试
	    glDepthFunc(GL_LEQUAL);							// 所作深度测试的类型
	    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);			// 告诉系统对透视进行修正
#endif

#ifndef USING_ES11
	    gProgram = createProgram(gVertexShader, gFragmentShader);
	    if (!gProgram) {
	        LOGE("Could not create program.");
	        return ;
	    }
	    gPositionHandle = glGetAttribLocation(gProgram, "a_Position");
	    checkGlError("glGetAttribLocation");
	    LOGI("glGetAttribLocation(\"a_Position\") = %d\n",
	            gPositionHandle);
	    gTexCoordsHandle = glGetAttribLocation(gProgram, "a_TexCoords");
	    checkGlError("glGetAttribLocation");
	    LOGI("glGetAttribLocation(\"a_TexCoords\") = %d\n",
	    		gTexCoordsHandle);
	    gTexHandle = glGetUniformLocation(gProgram, "u_Texture");
	    checkGlError("glGetUniformLocation");
	    LOGI("glGetUniformLocation(\"u_Texture\") = %d\n",
	    		gTexHandle);
#endif

}

#ifdef USING_ES11
static void _gluPerspective(GLfloat fovy, GLfloat aspect, GLfloat zNear, GLfloat zFar)
{
	GLfloat top = zNear * ((GLfloat) tan(fovy * PI / 360.0));
	GLfloat bottom = -top;
	GLfloat left = bottom * aspect;
	GLfloat right = top * aspect;
	glFrustumf(left, right, bottom, top, zNear, zFar);
}
#endif

static void resize(int w, int h)
{
	   if (h==0)								// 防止被零除
	  {
			  h=1;							// 将Height设为1
	  }

	  glViewport(0, 0, w, h);					// 重置当前的视口

#if USING_ES11

	  glMatrixMode(GL_PROJECTION);						// 选择投影矩阵
	  glLoadIdentity();							// 重置投影矩阵

	  GLfloat ratio = (GLfloat)w/(GLfloat)h;
	  // 设置视口的大小
	  _gluPerspective(45.0f,(GLfloat)w/(GLfloat)h,0.1f,100.0f);
  //    glOrthof(-2.0f, 2.0f, -2.0f, 2.0f, -2.0f, 2.0f);

	  glMatrixMode(GL_MODELVIEW);						// 选择模型观察矩阵
	  glLoadIdentity();							// 重置模型观察矩阵
#endif

}

static void update()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

#ifdef USING_ES11
	glLoadIdentity();
	glTranslatef(0, 0, -3.0f);

	glEnable(GL_TEXTURE_2D);
    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glVertexPointer(2, GL_FLOAT, 0, gVertices);
    glTexCoordPointer(2, GL_FLOAT, 0, gTexCoords);
	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
#else
    glUseProgram(gProgram);
    checkGlError("glUseProgram");
    glVertexAttribPointer(gPositionHandle, 2, GL_FLOAT, GL_FALSE, 0, gVertices);
    checkGlError("glVertexAttribPointer");
    glEnableVertexAttribArray(gPositionHandle);
    checkGlError("glEnableVertexAttribArray");
    glVertexAttribPointer(gTexCoordsHandle, 2, GL_FLOAT, GL_FALSE, 0, gTexCoords);
    checkGlError("glVertexAttribPointer");
    glEnableVertexAttribArray(gTexCoordsHandle);
    checkGlError("glEnableVertexAttribArray");

    glActiveTexture(GL_TEXTURE0);
    glUniform1i(gTexHandle, 0);

	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

#endif
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
