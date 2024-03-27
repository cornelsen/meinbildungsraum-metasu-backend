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

package de.cornelsen.nep.model.dto.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum SchoolType {
	GERMAN_SCHOOL_1("Kindergarten", "Kindergarten"),
	GERMAN_SCHOOL_2("Vorschule", "Vorschule"),
	GERMAN_SCHOOL_3("Grundschule", "Grundschule"),
	GERMAN_SCHOOL_4("Förderschule Lernen", "Förderschule Lernen"),
	GERMAN_SCHOOL_5("Hauptschule", "Hauptschule"),
	GERMAN_SCHOOL_6("Realschule", "Mittlere Schulform"),
	GERMAN_SCHOOL_7("Gymnasium", "Gymnasium"),
	GERMAN_SCHOOL_8("Integrierte Gesamtschule", "Gymnasium"),
	GERMAN_SCHOOL_9("Gymnasiale Oberstufe", "Gymnasiale Oberstufe"),
	GERMAN_SCHOOL_10("Fachoberschule", "Fachoberschule"),
	GERMAN_SCHOOL_11("Berufsschule", "Berufsschule"),
	GERMAN_SCHOOL_12("Förderschule emotionale und soziale Entwicklung", "Förderschule emotionale und soziale Entwicklung"),
	GERMAN_SCHOOL_13("Förderschule geistige Entwicklung", "Förderschule geistige Entwicklung"),
	GERMAN_SCHOOL_14("Förderschule Sprache", "Förderschule Sprache");

	protected static final SchoolType[] VALUES = SchoolType.values();

	private final String defaultValue;
	private final String dufValue;

	@JsonCreator
	public static SchoolType fromString(String value) {
		for (SchoolType schoolType : VALUES) {
			if (schoolType.getDefaultValue().equals(value)) {
				return schoolType;
			}
		}
		log.warn("Unsupported SchoolType for value: " + value);
		return null;
	}

	@Override
	public String toString() {
		return this.defaultValue;
	}
}
