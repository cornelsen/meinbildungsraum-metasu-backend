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

package de.cornelsen.nep.repository;

import de.cornelsen.nep.model.dto.rating.RatingStats;
import de.cornelsen.nep.model.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
	@Query("from Rating r where r.publisherId = ?1 and r.itemId = ?2 and (r.rating in ?3 or ?4 = true)")
	Page<Rating> findAllByPublisherIdAndItemIdAndRating(Long publisherId, String itemId, short[] rating, boolean all, Pageable pageable);

	Page<Rating> findAllByUserIdOrderByModifiedAtDesc(String userId, Pageable pageable);

	Optional<Rating> findByPublisherIdAndItemIdAndUserId(Long publisherId, String itemId, String userId);

	@Query("select new de.cornelsen.nep.model.dto.rating.RatingStats(rating, COUNT(id)) from Rating where publisherId = ?1 and itemId = ?2 group by rating")
	List<RatingStats> countItemRatings(Long publisherId, String itemId);

	@Query("select count(*) from Rating where publisherId = ?1 and itemId = ?2")
	long countTotalRatings(Long publisherId, String itemId);

	@Query("select count(*) from Rating where publisherId = ?1 and itemId = ?2 and text is null")
	long countEmptyRatings(Long publisherId, String itemId);

}
