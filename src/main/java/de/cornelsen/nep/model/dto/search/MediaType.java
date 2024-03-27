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

import java.io.Serializable;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum MediaType implements Serializable {

	INTERACTIVE_ANIMATION("Interactive/Animation"),
	INTERACTIVE_SIMULATION("Interactive/Simulation"),
	INTERACTIVE_WHITEBOARD("Interactive/Whiteboard"),
	MEDIA_AUDIO("Media/Audio"),
	MEDIA_IMAGES_VISUALS("Media/Images/Visuals"),
	MEDIA_VIDEO("Media/Video"),
	TEXT_BOOK("Text/Book"),
	TEXT_CHAPTER("Text/Chapter"),
	TEXT_DOCUMENT("Text/Document"),
	TEXT_PASSAGE("Text/Passage"),
	TEXT_REFERENCE("Text/Reference"),
	TEXT_WEBSITE("Text/Website");

	protected static final MediaType[] VALUES = MediaType.values();

	private final String label;

	@JsonCreator
	public static MediaType fromValue(String value) {
		for (MediaType type : VALUES) {
			if (type.getLabel().equals(value)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
