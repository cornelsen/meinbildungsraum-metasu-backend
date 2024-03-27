/*
 * Copyright (c) 2023-2024 Cornelsen Verlag GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cornelsen.nep.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum LRTEnum {
	ACTIVITY_EXPERIMENT("Activity/Experiment", "Activity/Experiment"),
	ACTIVITY_LEARNING("Activity/Learning", "Activity/Learning"),
	ACTIVITY_WORKSHEET("Activity/Worksheet", "Activity/Worksheet"),
	ASSESSMENT_FORMATIVE("Assessment/Formative", "Überblicksdiagnose"),
	ASSESSMENT_INTERIM("Assessment/Interim", "Detaildiagnose"),
	ASSESSMENT_ITEM("Assessment/Item", "Assessment/Item"),
	ASSESSMENT_PREPARATION("Assessment/Preparation", "Test zum Thema"),
	ASSESSMENT_RUBRIC("Assessment/Rubric", "Assessment/Rubric"),
	COLLECTION_COURSE("Collection/Course", "Collection/Course"),
	COLLECTION_CURRICULUM_GUIDE("Collection/Curriculum Guide", "Collection/Curriculum Guide"),
	COLLECTION_LESSON("Collection/Lesson", "Collection/Lesson"),
	COLLECTION_UNIT("Collection/Unit", "Collection/Unit"),
	GAME("Game", "Game"),
	LECTURE("Lecture", "Lecture");

	protected static final LRTEnum[] VALUES = LRTEnum.values();

	private final String defaultValue;
	private final String dufValue;

	@JsonCreator
	public static LRTEnum fromString(String value) {
		for (LRTEnum lrt : VALUES) {
			if (lrt.getDefaultValue().equals(value)) {
				return lrt;
			}
		}
		log.warn("Unsupported LRTEnum for value: " + value);
		return null;
	}
}
