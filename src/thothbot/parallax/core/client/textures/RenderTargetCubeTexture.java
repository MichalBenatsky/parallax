/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file based on the JavaScript source file of the THREE.JS project, 
 * licensed under MIT License.
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Parallax. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.core.client.textures;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.gl2.WebGLFramebuffer;
import thothbot.parallax.core.client.gl2.WebGLRenderbuffer;
import thothbot.parallax.core.client.gl2.WebGLRenderingContext;
import thothbot.parallax.core.client.gl2.enums.GLenum;
import thothbot.parallax.core.shared.core.Mathematics;


public class RenderTargetCubeTexture extends RenderTargetTexture
{
	private int activeCubeFace = 0;

	private List<WebGLFramebuffer> webglFramebuffer;
	private List<WebGLRenderbuffer> webglRenderbuffer;

	public RenderTargetCubeTexture(int width, int height ) 
	{
		super(width, height);
	}
	
	public int getActiveCubeFace() {
		return this.activeCubeFace;
	}
	
	public void setActiveCubeFace(int activeCubeFace) {
		this.activeCubeFace = activeCubeFace;
	}

	@Override
	public void deallocate(WebGLRenderingContext gl)
	{
		if (this.getWebGlTexture() == null)
			return;
	
		gl.deleteTexture(this.getWebGlTexture());
		for (int i = 0; i < 6; i++) 
		{
			gl.deleteFramebuffer(this.webglFramebuffer.get(i));
			gl.deleteRenderbuffer(this.webglRenderbuffer.get(i));
		}
	}
	
	@Override
	public WebGLFramebuffer getWebGLFramebuffer() 
	{
		return this.webglFramebuffer.get( getActiveCubeFace() );
	}
	
	@Override
	public void setRenderTarget(WebGLRenderingContext gl)
	{
		if (this.webglFramebuffer != null)
			return;

		this.setWebGlTexture(gl.createTexture());

		// Setup texture, create render and frame buffers
		boolean isTargetPowerOfTwo = Mathematics.isPowerOfTwo(getWidth())
				&& Mathematics.isPowerOfTwo(getHeight());

		this.webglFramebuffer = new ArrayList<WebGLFramebuffer>();
		this.webglRenderbuffer = new ArrayList<WebGLRenderbuffer>();

		gl.bindTexture( GLenum.TEXTURE_CUBE_MAP.getValue(), this.getWebGlTexture() );

		setTextureParameters( gl, GLenum.TEXTURE_CUBE_MAP.getValue(), isTargetPowerOfTwo );

		for ( int i = 0; i < 6; i ++ ) 
		{
			this.webglFramebuffer.add( gl.createFramebuffer() );
			this.webglRenderbuffer.add( gl.createRenderbuffer() );

			gl.texImage2D( GLenum.TEXTURE_CUBE_MAP_POSITIVE_X.getValue() + i, 0, getFormat().getValue(), getWidth(), getHeight(), 0, 
					getFormat().getValue(), getType().getValue(), null );

			this.setupFrameBuffer(gl, this.webglFramebuffer.get( i ), GLenum.TEXTURE_CUBE_MAP_POSITIVE_X.getValue() + i);
			this.setupRenderBuffer(gl, this.webglRenderbuffer.get( i ));
		}

		if ( isTargetPowerOfTwo ) 
			gl.generateMipmap( GLenum.TEXTURE_CUBE_MAP.getValue() );

		// Release everything
		gl.bindTexture( GLenum.TEXTURE_CUBE_MAP.getValue(), null );
		gl.bindRenderbuffer(GLenum.RENDERBUFFER.getValue(), null);
		gl.bindFramebuffer(GLenum.FRAMEBUFFER.getValue(), null);
	}
	
	@Override
	public void updateRenderTargetMipmap(WebGLRenderingContext gl) 
	{	
		gl.bindTexture( GLenum.TEXTURE_CUBE_MAP.getValue(), this.getWebGlTexture() );
		gl.generateMipmap( GLenum.TEXTURE_CUBE_MAP.getValue() );
		gl.bindTexture( GLenum.TEXTURE_CUBE_MAP.getValue(), null );
	}
}
