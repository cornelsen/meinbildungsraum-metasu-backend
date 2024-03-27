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

package de.cornelsen.nep.model.dto.lti;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The labelled curriculum standard GUID.
 */
@Data
public class LabelledGUIDType {
	/**
	 * A human readable string to provide a clue about the nature of the curriculum standard. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("label")
	private String label;

	/**
	 * The corresponding Competency and Academic Standards (CASE) URI. This is the URI used for alignment with the IMS CASE Service 1.0 specification [CASE, 17]. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("caseItemURI")
	private String caseItemURI;

	/**
	 * The GUID itself. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("GUID")
	private String guid;
}

