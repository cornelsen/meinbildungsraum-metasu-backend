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

package de.cornelsen.nep.service;

import de.cornelsen.nep.model.dto.SubjectRsp;
import de.cornelsen.nep.model.dto.filters.FiltersRsp;
import de.cornelsen.nep.model.dto.lti.SubjectType;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.request.LRTEnum;
import de.cornelsen.nep.model.dto.search.*;
import de.cornelsen.nep.model.dto.search.SchoolBook;
import de.cornelsen.nep.service.client.SubjectClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {

	private final SubjectClientService subjectClientService;
	private final PublisherService publisherService;

	private static final List<SubjectRsp> SUBJECTS = List.of(
		new SubjectRsp("LANGUAGES", List.of(
			Subject.DEUTSCH,
			Subject.DEUTSCH_ALS_ZWEITSPRACHE,
			Subject.ENGLISCH,
			Subject.FRANZOSISCH,
			Subject.GRIECHISCH,
			Subject.ITALIENISCH,
			Subject.LATEIN,
			Subject.RUSSISCH,
			Subject.SONSTIGE,
			Subject.SPANISCH,
			Subject.TURKISCH
		)),
		new SubjectRsp("STEM", List.of(
			Subject.BIOLOGIE,
			Subject.CHEMIE,
			Subject.INFORMATIK_ITB,
			Subject.MATHEMATIK,
			Subject.PHYSIK,
			Subject.SACHUNTERRICHT,
			Subject.UMWELT
		)),
		new SubjectRsp("SOCIAL_SCIENCES", List.of(
			Subject.GEOGRAFIE,
			Subject.GESCHICHTE,
			Subject.POLITISCHE_BILDUNG,
			Subject.WIRTSCHAFTSKUNDE
		)),
		new SubjectRsp("ARTS", List.of(
			Subject.BILDENDE_KUNST,
			Subject.MUSIK
		)),
		new SubjectRsp("ETHICS_PHILOSOPHY_RELIGION", List.of(
			Subject.ETHIK,
			Subject.PHILOSOPHIE,
			Subject.RELIGION
		)),
		new SubjectRsp("SPORTS", List.of(
			Subject.SPORT
		)),
		new SubjectRsp("OTHERS", List.of(
			Subject.ARBEITSLEHRE,
			Subject.BERUFLICHE_BILDUNG,
			Subject.ELEMENTARBEREICH_VORSCHULERZIEHUNG,
			Subject.FREIZEIT,
			Subject.GESUNDHEIT,
			Subject.GRUNDSCHULE,
			Subject.HEIMATRAUM_REGION,
			Subject.INTERKULTURELLE_BILDUNG,
			Subject.KINDER_UND_JUGENDBILDUNG,
			Subject.MEDIENPADAGOGIK,
			Subject.PADAGOGIK,
			Subject.PRAXISORIENTIERTE_FACHER,
			Subject.PSYCHOLOGIE,
			Subject.RETTEN_HELFEN_SCHUTZEN,
			Subject.SPIEL_UND_DOKUMENTARFILM,
			Subject.SUCHT_UND_PRAVENTION,
			Subject.UBERGREIFENDE_THEMEN,
			Subject.VERKEHRSERZIEHUNG,
			Subject.WEITERBILDUNG
		))
	);

	public FiltersRsp getFiltersList() {
		return new FiltersRsp(getSubjectsList(),
			getMediaTypesList(),
			getLearningResourceTypesList(),
			getSchoolTypes(),
			getStudyYear(),
			getPublishers(),
			getFederalStates(),
			getSchoolBooks(),
			getGrade(),
			getDifferentiation()
		);
	}

	/**
	 * Takes an intersection of two lists of subjects.
	 * Former - static, predefined, and latter - merged list of subjects from all api /subject resources
	 *
	 * @return list of subjects
	 */
	private List<SubjectRsp> getSubjectsList() {

		List<PublisherRsp> publishers = publisherService.getActivePublishers();
		Set<String> subjectsRsp = subjectClientService.subjects(publishers).stream().distinct().map(SubjectType::getName).collect(Collectors.toSet());

		ArrayList<SubjectRsp> supportedSubjectsList = new ArrayList<>(SUBJECTS);
		supportedSubjectsList.forEach(subject -> {
			List<Subject> filteredItems = subject.getItems().stream().filter(s -> subjectsRsp.contains(s.getLtiValue())).toList();
			subject.setItems(filteredItems);
		});
		return supportedSubjectsList;
	}

	private List<String> getMediaTypesList() {
		return Arrays.stream(MediaType.values())
			.map(MediaType::name)
			.toList();
	}

	private List<String> getSchoolTypes() {
		return Arrays.stream(SchoolType.values())
			.map(SchoolType::name)
			.toList();
	}

	private List<String> getStudyYear() {
		return Arrays.stream(StudyYear.values())
			.map(StudyYear::name)
			.toList();
	}

	private List<String> getLearningResourceTypesList() {
		return Arrays.stream(LRTEnum.values())
			.map(LRTEnum::name)
			.toList();
	}

	private List<String> getFederalStates() {
		return Arrays.stream(FederalState.values())
			.map(FederalState::name)
			.toList();
	}

	private List<String> getSchoolBooks() {
		return Arrays.stream(SchoolBook.values())
			.map(SchoolBook::name)
			.toList();
	}

	private List<String> getGrade() {
		return Arrays.stream(Grade.values())
			.map(Grade::name)
			.toList();
	}

	private List<String> getDifferentiation() {
		return Arrays.stream(Differentiation.values())
			.map(Differentiation::name)
			.toList();
	}

	private List<PublisherRsp> getPublishers() {
		return publisherService.getActivePublishers();
	}

}
