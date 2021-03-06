/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CatroidFieldKeySorter implements FieldKeySorter {

	private static final String TAG = CatroidFieldKeySorter.class.getSimpleName();

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map sort(final Class type, final Map keyedByFieldKey) {
		if (type.equals(Project.class)) {
			return sortProjectFields(keyedByFieldKey);
		}

		if (type.equals(Scene.class)) {
			return sortSceneFields(keyedByFieldKey);
		}

		if (type.equals(Sprite.class)) {
			return sortSpriteFields(keyedByFieldKey);
		}

		final Map map = new TreeMap(new Comparator() {

			@Override
			public int compare(final Object objectOne, final Object objectTwo) {
				final FieldKey fieldKeyOne = (FieldKey) objectOne;
				final FieldKey fieldKeyTwo = (FieldKey) objectTwo;
				int fieldKeyComparator = fieldKeyOne.getDepth() - fieldKeyTwo.getDepth();
				if (fieldKeyComparator == 0) {
					String fieldNameOrAlias1 = getFieldNameOrAlias(fieldKeyOne);
					String fieldNameOrAlias2 = getFieldNameOrAlias(fieldKeyTwo);
					fieldKeyComparator = fieldNameOrAlias1.compareTo(fieldNameOrAlias2);
				}
				return fieldKeyComparator;
			}
		});
		map.putAll(keyedByFieldKey);
		return map;
	}

	private String getFieldNameOrAlias(FieldKey fieldKey) {
		String fieldName = fieldKey.getFieldName();
		try {
			Field field = fieldKey.getDeclaringClass().getDeclaredField(fieldName);

			XStreamAlias alias = field.getAnnotation(XStreamAlias.class);
			if (alias != null) {
				return alias.value();
			} else {
				return fieldName;
			}
		} catch (SecurityException | NoSuchFieldException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
		}
		return fieldName;
	}

	private Map sortProjectFields(Map map) {
		Map orderedMap = new OrderRetainingMap();
		FieldKey[] fieldKeyOrder = new FieldKey[map.size()];
		Iterator<FieldKey> iterator = map.keySet().iterator();
		int fieldPos = 0;
		while (iterator.hasNext()) {
			FieldKey fieldKey = iterator.next();
			if (fieldKey.getFieldName().equals("xmlHeader")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("serialVersionUID")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("settings")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("$change")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("sceneList")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("projectVariables")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			} else if (fieldKey.getFieldName().equals("projectLists")) {
				fieldKeyOrder[fieldPos] = fieldKey;
				fieldPos++;
			}
		}
		for (FieldKey fieldKey : fieldKeyOrder) {
			orderedMap.put(fieldKey, map.get(fieldKey));
		}
		return orderedMap;
	}

	private Map sortSceneFields(Map map) {
		Map orderedMap = new OrderRetainingMap();
		FieldKey[] fieldKeyOrder = new FieldKey[map.size()];
		Iterator<FieldKey> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			FieldKey fieldKey = iterator.next();
			if (fieldKey.getFieldName().equals("sceneName")) {
				fieldKeyOrder[0] = fieldKey;
			} else if (fieldKey.getFieldName().equals("spriteList")) {
				fieldKeyOrder[1] = fieldKey;
			} else if (fieldKey.getFieldName().equals("dataContainer")) {
				fieldKeyOrder[2] = fieldKey;
			} else if (fieldKey.getFieldName().equals("physicsWorld")) {
				fieldKeyOrder[3] = fieldKey;
			} else if (fieldKey.getFieldName().equals("project")) {
				fieldKeyOrder[4] = fieldKey;
			} else if (fieldKey.getFieldName().equals("serialVersionUID")) {
				fieldKeyOrder[5] = fieldKey;
			} else if (fieldKey.getFieldName().equals("firstStart")) {
				fieldKeyOrder[6] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isBackPackScene")) {
				fieldKeyOrder[7] = fieldKey;
			} else if (fieldKey.getFieldName().equals("originalWidth")) {
				fieldKeyOrder[8] = fieldKey;
			} else if (fieldKey.getFieldName().equals("originalHeight")) {
				fieldKeyOrder[9] = fieldKey;
			} else if (fieldKey.getFieldName().equals("$change")) {
				fieldKeyOrder[10] = fieldKey;
			}
		}
		for (FieldKey fieldKey : fieldKeyOrder) {
			orderedMap.put(fieldKey, map.get(fieldKey));
		}
		return orderedMap;
	}

	private Map sortSpriteFields(Map map) {
		Map orderedMap = new OrderRetainingMap();
		FieldKey[] fieldKeyOrder = new FieldKey[map.size()];
		Iterator<FieldKey> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			FieldKey fieldKey = iterator.next();
			if (fieldKey.getFieldName().equals("TAG")) {
				fieldKeyOrder[0] = fieldKey;
			} else if (fieldKey.getFieldName().equals("serialVersionUID")) {
				fieldKeyOrder[1] = fieldKey;
			} else if (fieldKey.getFieldName().equals("spriteFactory")) {
				fieldKeyOrder[2] = fieldKey;
			} else if (fieldKey.getFieldName().equals("look")) {
				fieldKeyOrder[3] = fieldKey;
			} else if (fieldKey.getFieldName().equals("name")) {
				fieldKeyOrder[4] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isPaused")) {
				fieldKeyOrder[5] = fieldKey;
			} else if (fieldKey.getFieldName().equals("convertToSingleSprite")) {
				fieldKeyOrder[6] = fieldKey;
			} else if (fieldKey.getFieldName().equals("convertToGroupItemSprite")) {
				fieldKeyOrder[7] = fieldKey;
			} else if (fieldKey.getFieldName().equals("lookList")) {
				fieldKeyOrder[8] = fieldKey;
			} else if (fieldKey.getFieldName().equals("soundList")) {
				fieldKeyOrder[9] = fieldKey;
			} else if (fieldKey.getFieldName().equals("scriptList")) {
				fieldKeyOrder[10] = fieldKey;
			} else if (fieldKey.getFieldName().equals("userBricks")) {
				fieldKeyOrder[11] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isClone")) {
				fieldKeyOrder[12] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isBackpackObject")) {
				fieldKeyOrder[13] = fieldKey;
			} else if (fieldKey.getFieldName().equals("penConfiguration")) {
				fieldKeyOrder[14] = fieldKey;
			} else if (fieldKey.getFieldName().equals("cloneForScene")) {
				fieldKeyOrder[15] = fieldKey;
			} else if (fieldKey.getFieldName().equals("nfcTagList")) {
				fieldKeyOrder[16] = fieldKey;
			} else if (fieldKey.getFieldName().equals("actionFactory")) {
				fieldKeyOrder[17] = fieldKey;
			} else if (fieldKey.getFieldName().equals("isMobile")) {
				fieldKeyOrder[18] = fieldKey;
			} else if (fieldKey.getFieldName().equals("$change")) {
				fieldKeyOrder[19] = fieldKey;
			}
		}
		for (FieldKey fieldKey : fieldKeyOrder) {
			orderedMap.put(fieldKey, map.get(fieldKey));
		}
		return orderedMap;
	}
}
