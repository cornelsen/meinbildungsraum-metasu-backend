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
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum SchoolBook {
	SCHOOL_BOOK_1("Access"),
	SCHOOL_BOOK_2("À Plus Neue Ausgabe"),
	SCHOOL_BOOK_3("À toi Neue Ausgabe"),
	SCHOOL_BOOK_4("Förderschule Lernen"),
	SCHOOL_BOOK_5("Biosphäre"),
	SCHOOL_BOOK_6("Chemie Fokus"),
	SCHOOL_BOOK_7("Deutschbuch GYM"),
	SCHOOL_BOOK_8("Deutschbuch MSF"),
	SCHOOL_BOOK_9("DoppelKlick"),
	SCHOOL_BOOK_10("Dreifach Mathe"),
	SCHOOL_BOOK_11("D wie Deutsch"),
	SCHOOL_BOOK_12("Forum Geschichte"),
	SCHOOL_BOOK_13("Fundamente der Mathematik"),
	SCHOOL_BOOK_14("Headlight"),
	SCHOOL_BOOK_15("Highlight"),
	SCHOOL_BOOK_16("Lehrwerksunabhängig"),
	SCHOOL_BOOK_17("Lighthouse"),
	SCHOOL_BOOK_18("Muttersprache plus"),
	SCHOOL_BOOK_19("Parallelo"),
	SCHOOL_BOOK_20("Parallelo Basis"),
	SCHOOL_BOOK_21("Physik Universum");

	protected static final SchoolBook[] VALUES = SchoolBook.values();

	@JsonValue
	private final String defaultValue;

	@JsonCreator
	public static SchoolBook fromValue(String value) {
		for (SchoolBook schoolBook : VALUES) {
			if (schoolBook.getDefaultValue().equals(value)) {
				return schoolBook;
			}
		}
		log.warn("Unsupported SchoolBook for value: {}", value);
		return null;
	}

}
