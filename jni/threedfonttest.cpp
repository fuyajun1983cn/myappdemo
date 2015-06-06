#include <jni.h>
#include <android/log.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>
#include <wctype.h>
#include <string.h>


#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H


#define MAX_NO_TEXTURES 1

GLuint texture_id[MAX_NO_TEXTURES];

struct xCharTexture {
	GLuint m_texID;
	wchar_t m_chaID;
	int m_Width;
	int m_Height;

	int m_adv_x;
	int m_adv_y;
	int m_delta_x;
	int m_delta_y;
public:
xCharTexture()
{
	m_texID = 0;
	m_chaID = 0;
	m_Width = 0;
	m_Height = 0;
}
};

class xFreeTypeLib
{
	FT_Library m_FT2Lib;
	FT_Face m_FT_Face;

	int m_w;
	int m_h;
public:

	xCharTexture g_TexID[65536];

	xFreeTypeLib()
	{
		if (FT_Init_FreeType( &m_FT2Lib) ) {
			__android_log_print(ANDROID_LOG_ERROR,"ThreeDFontTest","failed to init freetype\n");
			exit(0);
		}
	}

	void load(const char* font_file , int _w , int _h)
	{

		if (FT_New_Face( m_FT2Lib, font_file, 0, &m_FT_Face ))
		{
			__android_log_print(ANDROID_LOG_ERROR,"ThreeDFontTest","failed to load font file\n");
			exit(0);
		}
		FT_Select_Charmap(m_FT_Face, FT_ENCODING_UNICODE);
		m_w = _w; m_h = _h;

		//FT_Set_Char_Size( m_FT_Face , 0 , m_w << 6, 96, 96);
		FT_Set_Pixel_Sizes(m_FT_Face,m_w, m_h);
	}

	GLuint loadChar(wchar_t ch)
	{
		if(g_TexID[ch].m_texID) {
			GLuint tex[1] = {g_TexID[ch].m_texID};
			glDeleteTextures(1, tex);
		}

		if(FT_Load_Char(m_FT_Face, ch,FT_LOAD_RENDER|FT_LOAD_FORCE_AUTOHINT|
						(true ? FT_LOAD_TARGET_NORMAL : FT_LOAD_MONOCHROME | FT_LOAD_TARGET_MONO) ) )
		{
			__android_log_print(ANDROID_LOG_ERROR,"ThreeDFontTest","load char failed\n");
			return 0;
		}

		xCharTexture& charTex = g_TexID[ch];


		FT_Glyph glyph;
		if(FT_Get_Glyph( m_FT_Face->glyph, &glyph )) {
			__android_log_print(ANDROID_LOG_ERROR,"ThreeDFontTest","FT_Get_Glyph failed\n");
			return 0;
		}

		FT_Render_Glyph( m_FT_Face->glyph, FT_RENDER_MODE_LCD );//FT_RENDER_MODE_NORMAL  );
		FT_Glyph_To_Bitmap( &glyph, ft_render_mode_normal, 0, 1 );
		FT_BitmapGlyph bitmap_glyph = (FT_BitmapGlyph)glyph;

		FT_Bitmap& bitmap=bitmap_glyph->bitmap;


		int width = bitmap.width;
		int height = bitmap.rows;

		charTex.m_Width = width;
		charTex.m_Height = height;
		charTex.m_adv_x = m_FT_Face->glyph->advance.x / 64.0f;
		charTex.m_adv_y = m_FT_Face->size->metrics.y_ppem;//m_FT_Face->glyph->metrics.horiBearingY / 64.0f;
		charTex.m_delta_x = (float)bitmap_glyph->left;
		charTex.m_delta_y = (float)bitmap_glyph->top - height;
		glGenTextures(1,&charTex.m_texID);
		glBindTexture(GL_TEXTURE_2D,charTex.m_texID);
		char* pBuf = new char[width * height * 4];
		for(int j=0; j < height; j++)
		{
			for(int i=0; i < width; i++)
			{
				unsigned char _vl = (i>=bitmap.width || j>=bitmap.rows) ? 0 : bitmap.buffer[i + bitmap.width*j];
				pBuf[(4*i + (height - j - 1) * width * 4) ] = 0xff;
				pBuf[(4*i + (height - j - 1) * width * 4)+1] = 0xff;
				pBuf[(4*i + (height - j - 1) * width * 4)+2] = 0xff;
				pBuf[(4*i + (height - j - 1) * width * 4)+3] = _vl;
			}
		}

		glTexImage2D( GL_TEXTURE_2D,0,GL_RGBA,width, height,0,GL_RGBA,GL_UNSIGNED_BYTE,pBuf);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glTexEnvi(GL_TEXTURE_2D,GL_TEXTURE_ENV_MODE,GL_REPLACE);

		delete []pBuf;
		return charTex.m_chaID;

	}

} g_FreeTypeLib1, g_FreeTypeLib2;

/*fontId: 1, 2*/
xCharTexture* getTextChar(wchar_t ch, int fontId) {
	switch (fontId) {
	case 1:
		g_FreeTypeLib1.loadChar(ch);
		return &g_FreeTypeLib1.g_TexID[ch];
	case 2:
		g_FreeTypeLib2.loadChar(ch);
		return &g_FreeTypeLib2.g_TexID[ch];
	}
	return 0;
}

wchar_t g_UnicodeString1[] = L"字体测试   微软雅黑";
wchar_t g_UnicodeString2[] = L"字体测试   方正简宋";

size_t mywcslen(wchar_t *s) {
	 size_t len = 0;

	  while (s[len] != L'\0')
		{
		  if (s[++len] == L'\0')
		return len;
		  if (s[++len] == L'\0')
		return len;
		  if (s[++len] == L'\0')
		return len;
		  ++len;
		}

	  return len;
}

void drawText(wchar_t* _strText, int x, int y, int maxW, int h, int fontId) {
	int sx = x;
	int sy = y;
	int maxH = h;
	for (int i = 0; i < (int)mywcslen(_strText); i++) {

		if (_strText[i] == L'\n') {
			sx = x;
			sy += maxH + 12;
			continue;
		}

		xCharTexture* pCharTex = getTextChar(_strText[i], fontId);
		glBindTexture(GL_TEXTURE_2D, pCharTex->m_texID);
		__android_log_print(ANDROID_LOG_INFO,"ThreeDFontTest","TextureId: %d\n", pCharTex->m_texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		int w = pCharTex->m_Width;
		int h = pCharTex->m_Height;

		int ch_x = sx + pCharTex->m_delta_x;
		int ch_y = sy - h - pCharTex->m_delta_y;

		if (maxH < h)
			maxH = h;
		float texCoords[] = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
		float vertices[] = {ch_x, ch_y, 1.0f, ch_x + w, ch_y, 1.0f, ch_x + w, ch_y + h, 1.0f, ch_x, ch_y + h, 1.0f};
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, vertices);
		glTexCoordPointer(2, GL_FLOAT, 0, texCoords);
		glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);

		sx += pCharTex->m_adv_x;
		if (sx > x + maxW) {
			sx = x;
			sy += maxH + 12;
		}

	}

}

void init3DFont(void) {
	glShadeModel(GL_SMOOTH); // Enable Smooth Shading
	glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
	glEnable(GL_COLOR_MATERIAL);
	glEnable(GL_TEXTURE_2D);

	g_FreeTypeLib1.load("/system/fonts/simhei.ttf", 24, 24);
	g_FreeTypeLib2.load("/system/fonts/fzjt.ttf", 24, 24);

	glDisable(GL_CULL_FACE);

}

void reshape3DFont(int w, int h) {

	if (h == 0)
		h = 1;
	// Reset the coordinate system before modifying
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	// Set the viewport to be the entire window
	glViewport(0, 0, w, h);
	glOrthof(0, w, 0, h, -1, 1);
	// Set the clipping volume
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

}

void display3DFont(void) {

	glClearColor(0.0f, 0.0f, 0.6f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glLoadIdentity ();

	glEnable(GL_TEXTURE_2D);
	drawText(g_UnicodeString1,200,300,1280,25, 1);
	drawText(g_UnicodeString2,200,500,1280,25, 2);

	glFlush();
}


extern "C" {
    JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_resize(JNIEnv * env, jobject obj,  jint width, jint height);
    JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_draw(JNIEnv * env, jobject obj);
    JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_init(JNIEnv * env, jobject obj);
};

JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_resize(JNIEnv * env, jobject obj,  jint width, jint height)
{
	reshape3DFont(width, height);
}

JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_draw(JNIEnv * env, jobject obj)
{
	display3DFont();
}

JNIEXPORT void JNICALL Java_com_fyj_demo_es1_GL10JNILib_init(JNIEnv * env, jobject obj)
{
	init3DFont();
}

