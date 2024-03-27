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
public enum Subject {

	DEUTSCH("Deutsch"),
	DEUTSCH_ALS_ZWEITSPRACHE("Deutsch als Zweitsprache"),
	ENGLISCH("Englisch"),
	FRANZOSISCH("Französisch"),
	GRIECHISCH("Griechisch"),
	ITALIENISCH("Italienisch"),
	LATEIN("Latein"),
	RUSSISCH("Russisch"),
	SONSTIGE("Sonstige"),
	SPANISCH("Spanisch"),
	TURKISCH("Türkisch"),

	BIOLOGIE("Biologie"),
	CHEMIE("Chemie"),
	INFORMATIK_ITB("Informatik/ITB"),
	MATHEMATIK("Mathematik"),
	PHYSIK("Physik"),
	SACHUNTERRICHT("Sachunterricht"),
	UMWELT("Umwelt"),

	GEOGRAFIE("Geografie"),
	GESCHICHTE("Geschichte"),
	POLITISCHE_BILDUNG("Politische Bildung"),
	WIRTSCHAFTSKUNDE("Wirtschaftskunde"),

	BILDENDE_KUNST("Bildende Kunst"),
	MUSIK("Musik"),

	ETHIK("Ethik"),
	PHILOSOPHIE("Philosophie"),
	RELIGION("Religion"),

	SPORT("Sport"),

	ARBEITSLEHRE("Arbeitslehre"),
	BERUFLICHE_BILDUNG("Berufliche Bildung"),
	ELEMENTARBEREICH_VORSCHULERZIEHUNG("Elementarbereich, Vorschulerziehung"),
	FREIZEIT("Freizeit"),
	GESUNDHEIT("Gesundheit"),
	GRUNDSCHULE("Grundschule"),
	HEIMATRAUM_REGION("Heimatraum, Region"),
	INTERKULTURELLE_BILDUNG("Interkulturelle Bildung"),
	KINDER_UND_JUGENDBILDUNG("Kinder- und Jugendbildung"),
	MEDIENPADAGOGIK("Medienpädagogik"),
	PADAGOGIK("Pädagogik"),
	PRAXISORIENTIERTE_FACHER("Praxisorientierte Fächer"),
	PSYCHOLOGIE("Psychologie"),
	RETTEN_HELFEN_SCHUTZEN("Retten, Helfen, Schützen"),
	SPIEL_UND_DOKUMENTARFILM("Spiel- und Dokumentarfilm"),
	SUCHT_UND_PRAVENTION("Sucht und Prävention"),
	UBERGREIFENDE_THEMEN("Übergreifende Themen"),
	VERKEHRSERZIEHUNG("Verkehrserziehung"),
	WEITERBILDUNG("Weiterbildung");

	protected static final Subject[] VALUES = Subject.values();
	private final String ltiValue;

	@Override
	public String toString() {
		return ltiValue;
	}

	@JsonCreator
	public static Subject fromString(String value) {
		for (Subject subject : VALUES) {
			if (subject.getLtiValue().equals(value)) {
				return subject;
			}
		}
		log.warn("Unsupported Subject for value: " + value);
		return null;
	}
}
