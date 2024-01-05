/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

import java.util.ArrayList;
import java.util.List;

public class Ballistica {

	//note that the path is the FULL path of the projectile, including tiles after collision.
	//make sure to generate a subPath for the common case of going source to collision.
	public ArrayList<Integer> path = new ArrayList<>();
	public Integer sourcePos = null;
	public Integer collisionPos = null;
	public Integer collisionProperties = null;
	public Integer dist = 0;

	//parameters to specify the colliding cell
	public static final int STOP_TARGET = 1;    //ballistica will stop at the target cell
	public static final int STOP_CHARS = 2;     //ballistica will stop on first char hit
	public static final int STOP_SOLID = 4;     //ballistica will stop on solid terrain
	public static final int IGNORE_SOFT_SOLID = 8; //ballistica will ignore soft solid terrain, such as doors and webs
//	public static final int STOP_BARRIER_PLAYER = 16; //ballistica will stop on barriers which block the player
//	public static final int STOP_BARRIER_MOBS = 32; //ballistica will stop on barriers which block mobs
//	public static final int STOP_BARRIER_ALLIES = 64; //ballistica will stop on barriers which block allies
	public static final int STOP_BARRIER_PROJECTILES = 128; //ballistica will stop on barriers which block projectiles
//	public static final int STOP_BARRIER_BLOBS = 256; //ballistica will stop on barriers which block blobs

	public static final int PROJECTILE =    	STOP_TARGET	| STOP_CHARS	| STOP_SOLID;
	public static final int REAL_PROJECTILE =  	PROJECTILE | STOP_BARRIER_PROJECTILES;

	public static final int MAGIC_BOLT =        STOP_CHARS  | STOP_SOLID;
	public static final int REAL_MAGIC_BOLT =   MAGIC_BOLT | STOP_BARRIER_PROJECTILES;

	public static final int WONT_STOP =         0;


	public Ballistica( int from, int to, int params, Char usePassable ){
		sourcePos = from;
		collisionProperties = params;
		build(from, to,
				(params & STOP_TARGET) > 0,
				(params & STOP_CHARS) > 0,
				(params & STOP_SOLID) > 0,
				(params & IGNORE_SOFT_SOLID) > 0,
				(params & STOP_BARRIER_PROJECTILES) > 0,
				usePassable);

		if (collisionPos != null) {
			dist = path.indexOf(collisionPos);
		} else if (!path.isEmpty()) {
			collisionPos = path.get(dist = path.size() - 1);
		} else {
			path.add(from);
			collisionPos = from;
			dist = 0;
		}
	}

	private void build(int from, int to, boolean stopTarget, boolean stopChars, boolean stopTerrain, boolean ignoreSoftSolid, boolean stopBarrierProj, Char usePassable) {
		int w = Dungeon.level.width();

		int x0 = from % w;
		int x1 = to % w;
		int y0 = from / w;
		int y1 = to / w;

		int dx = x1 - x0;
		int dy = y1 - y0;

		int stepX = dx > 0 ? +1 : -1;
		int stepY = dy > 0 ? +1 : -1;

		dx = Math.abs( dx );
		dy = Math.abs( dy );

		int stepA;
		int stepB;
		int dA;
		int dB;

		if (dx > dy) {

			stepA = stepX;
			stepB = stepY * w;
			dA = dx;
			dB = dy;

		} else {

			stepA = stepY * w;
			stepB = stepX;
			dA = dy;
			dB = dx;

		}

		int cell = from;

		int err = dA / 2;
		while (Dungeon.level.insideMap(cell)) {

			//if we're in solid terrain, and there's no char there, collide with the previous cell.
			// we don't use solid here because we don't want to stop short of closed doors.
			if (collisionPos == null
					&& stopTerrain
					&& cell != sourcePos
					&& !Dungeon.level.isPassable(cell, usePassable)
					&& !Dungeon.level.avoid[cell]
					&& Actor.findChar(cell) == null) {
				collide(path.get(path.size() - 1));
			}
			if (!path.isEmpty() && collisionPos == null && stopBarrierProj && Dungeon.level.barriers.get(cell) != null && Dungeon.level.barriers.get(cell).blocksProjectiles()){
				collide(path.get(path.size() - 1));
			}

			path.add(cell);

			if (collisionPos == null && stopTerrain && cell != sourcePos && Dungeon.level.solid[cell]) {
				if (ignoreSoftSolid && (Dungeon.level.isPassable(cell, usePassable) || Dungeon.level.avoid[cell])) {
					//do nothing
				} else {
					collide(cell);
				}
			}
			if (collisionPos == null && cell != sourcePos && stopChars && Actor.findChar( cell ) != null) {
				collide(cell);
			}
			if (collisionPos == null && cell == to && stopTarget){
				collide(cell);
			}

			cell += stepA;

			err += dB;
			if (err >= dA) {
				err = err - dA;
				cell = cell + stepB;
			}
		}
	}

	//we only want to record the first position collision occurs at.
	private void collide(int cell){
		if (collisionPos == null) {
			collisionPos = cell;
		}
	}

	//returns a segment of the path from start to end, inclusive.
	//if there is an error, returns an empty arraylist instead.
	public List<Integer> subPath(int start, int end){
		try {
			end = Math.min( end, path.size()-1);
			return path.subList(start, end+1);
		} catch (Exception e){
			SandboxPixelDungeon.reportException(e);
			return new ArrayList<>();
		}
	}
}