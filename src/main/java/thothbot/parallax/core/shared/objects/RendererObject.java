/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the Creative Commons Attribution 
 * 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 
 * 3.0 Unported License along with Parallax. 
 * If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package thothbot.parallax.core.shared.objects;

import thothbot.parallax.core.shared.core.GeometryBuffer;
import thothbot.parallax.core.shared.core.GeometryGroup;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshFaceMaterial;

public class RendererObject implements Comparable<RendererObject>
{
	public GeometryObject object;
	public GeometryBuffer buffer;
	public boolean render;
	
	public Material opaque;
	public Material transparent;
	// render depth
	public double z;

	public RendererObject(GeometryBuffer buffer, GeometryObject object, Material opaque, Material transparent) 
	{
		this.buffer = buffer;
		this.object = object;
		this.opaque = opaque;
		this.transparent = transparent;
	}
	
	public void unrollBufferMaterial() 
	{
		Material meshMaterial = object.getMaterial();

		if ( meshMaterial instanceof MeshFaceMaterial ) 
		{
			int materialIndex = ((GeometryGroup)buffer).materialIndex;

			if ( materialIndex >= 0 ) 
			{
				Material material = object.getGeometry().getMaterials().get( materialIndex );

				if ( material.isTransparent() ) 
				{
					transparent = material;
					opaque = null;
					
				} 
				else 
				{
					opaque = material;
					transparent = null;
				}
			}
		} 
		else 
		{

			Material material = meshMaterial;

			if ( material != null) 
			{
				if ( material.isTransparent() ) 
				{
					transparent = material;
					opaque = null;

				} 
				else 
				{
					opaque = material;
					transparent = null;
				}
			}
		}
	}
	
	@Override
	public int compareTo(RendererObject o)
	{
		double result = o.z - this.z; 
		return (result == 0) ? 0 
				: (result > 0) ? 1 : -1;
	}
}
