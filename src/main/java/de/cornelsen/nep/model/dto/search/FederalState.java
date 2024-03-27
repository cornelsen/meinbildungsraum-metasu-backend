/*
 * Copyright (c) 2023 Cornelsen Verlag GmbH
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

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FederalState {
	BADEN_WURTEMBERG("Baden-Württemberg"),
	BAYERN("Bayern"),
	BERLIN("Berlin"),
	BRANDENBURG("Brandenburg"),
	BREMEN("Bremen"),
	HAMBURG("Hamburg"),
	HESSEN("Hessen"),
	MECKLENBURG_VORPOMMERN("Mecklenburg-Vorpommern"),
	NIEDERSACHSEN("Niedersachsen"),
	NORDRHEIN_WESTFALEN("Nordrhein-Westfalen"),
	RHEINLAND_PFALZ("Rheinland-Pfalz"),
	SAARLAND("Saarland"),
	SACHSEN("Sachsen"),
	SACHSEN_ANHALT("Sachsen-Anhalt"),
	SCHLESWIG_HOLSTEIN("Schleswig-Holstein"),
	THURINGEN("Thüringen");

	@JsonValue
	private final String label;

	@Override
	public String toString() {
		return label;
	}
}
