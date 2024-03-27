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

package de.cornelsen.nep.model.dto.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static de.cornelsen.nep.configuration.ApplicationConst.DEFAULT_DATE_FORMAT;

@Data
public class RatingRsp {

	private long emptyRatings;
	private long totalRatingsCount;
	private String rating;

	@Schema(description = "Item already rated by current logged user")
	private boolean rated;
	private Map<Integer, Long> votes = new HashMap<>();
	private Page<Comment> comments;

	public RatingRsp() {
		votes.put(1, 0L);
		votes.put(2, 0L);
		votes.put(3, 0L);
		votes.put(4, 0L);
		votes.put(5, 0L);
	}

	@Data
	public static class Comment {
		private String id;
		private int rating;
		private String text;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT)
		private LocalDateTime modifiedAt;
	}

	public void calculateRating() {
		if (votes.values().stream().mapToLong(Long::valueOf).sum() == 0) {
			return;
		}
		AtomicLong votesCount = new AtomicLong();
		AtomicLong votesSum = new AtomicLong();
		this.votes.forEach((rate, total) -> {
			votesCount.addAndGet(rate * total);
			votesSum.addAndGet(total);
		});
		setRating(String.format(Locale.ENGLISH, "%.1f", votesCount.get() * 1.0 / votesSum.get()));
	}
}
