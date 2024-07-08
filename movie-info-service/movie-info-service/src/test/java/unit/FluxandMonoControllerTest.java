package unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivesprive.movie_info_service.controller.FluxandMonoController;

import reactor.test.StepVerifier;

@WebFluxTest(FluxandMonoController.class)
@ContextConfiguration(classes = FluxandMonoController.class)
@AutoConfigureWebTestClient
class FluxandMonoControllerTest {

	@Autowired
	private WebTestClient webTestClient;

//    @Test
//    void testFlux() {
//        webTestClient.get().uri("/flux")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Integer.class)
//                .hasSize(3);
//    }
//    @Test
//    void testFlux() {
//       var flux= webTestClient.get().uri("/flux")
//                .exchange()
//                .expectStatus().isOk()
//                .returnResult(Integer.class)
//                .getResponseBody();
//       StepVerifier.create(flux).expectNext(1,2,3).verifyComplete();
//    }

	@Test
	void testFlux() {
		webTestClient.get().uri("/flux").exchange().expectStatus().isOk().expectBodyList(Integer.class)
				.consumeWith(response -> {
					var responseBody = response.getResponseBody();
					assert responseBody != null;
					assert responseBody.size() == 3 : "Expected size 3, but got " + responseBody.size();
				});
	}

	@Test
	void testMono() {
		webTestClient.get().uri("/mono").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hanuma");
	}

	@Test
	void testStream() {
		webTestClient.get().uri("/stream").exchange().expectStatus().isOk().expectHeader()
				.contentType("text/event-stream;charset=UTF-8");
	}

	@Test
	void streamTest() {
		var flux = webTestClient.get().uri("/stream").exchange().expectStatus().isOk().returnResult(Long.class)
				.getResponseBody();
		StepVerifier.create(flux).expectNext(0L, 1L, 2L).thenCancel().verify();
	}
}
