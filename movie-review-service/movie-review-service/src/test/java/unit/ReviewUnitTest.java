package unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactive.movie_review_service.ExceptionHandler.GlobalErrorHandler;
import com.reactive.movie_review_service.Repository.ReviewReactiveRepository;
import com.reactive.movie_review_service.domain.Review;
import com.reactive.movie_review_service.handler.ReviewHandler;
import com.reactive.movie_review_service.router.ReviewRouter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = { ReviewRouter.class, ReviewHandler.class,GlobalErrorHandler.class })
@AutoConfigureWebTestClient
public class ReviewUnitTest {
	@MockBean
	private ReviewReactiveRepository reactiveRepository;
	@Autowired
	private WebTestClient webTestClient;
	static String REVIEWS_URL = "/v1/reviews";

	@Test
	    void addReview() {
	        //given
		  var review = new Review("123", 1L, "Awesome Movie", 9.0);

		  when(reactiveRepository.save(any())).thenReturn(Mono.just(review));

	        //when
	        webTestClient
	                .post()
	                .uri(REVIEWS_URL)
	                .bodyValue(review)
	                .exchange()
	                .expectStatus().isCreated()
	                .expectBody(Review.class)
	                .consumeWith(reviewResponse -> {
	                    var savedReview = reviewResponse.getResponseBody();
	                    assert savedReview != null;
	                    assertNotNull(savedReview.getReviewId());
	                });

	    }
}
