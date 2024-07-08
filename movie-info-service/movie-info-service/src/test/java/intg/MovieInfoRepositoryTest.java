package intg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.reactivesprive.movie_info_service.MovieInfoServiceApplication;
import com.reactivesprive.movie_info_service.Repository.MovieInfoRepository;
import com.reactivesprive.movie_info_service.domain.MovieInfo;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = MovieInfoServiceApplication.class)

class MovieInfoRepositoryTest {
	@Autowired
	MovieInfoRepository infoRepository;

	
	@BeforeEach
	void setUp() {

		var movieinfos = List.of(
				new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
						LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
						LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
						LocalDate.parse("2012-07-20")));

		infoRepository.saveAll(movieinfos).blockLast();
	}

	@AfterEach
	void tearDown() {
		infoRepository.deleteAll().block();
	}
//	@Test
//	void saveMovieInfo() {
//
//		var movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"),
//				LocalDate.parse("2005-06-15"));
//
//		var savedMovieInfo = infoRepository.save(movieInfo).log();
//
//		StepVerifier.create(savedMovieInfo).assertNext(movieInfo1 -> {
//			assertNotNull(movieInfo1.getId());
//			assertEquals("Batman Begins1", movieInfo1.getId());
//			;
//		});
//
//	}
	@Test
	void findAll() {
		Flux<MovieInfo> fluxmovieinfo = infoRepository.findAll();
		StepVerifier.create(fluxmovieinfo).expectNextCount(3).verifyComplete();
	}

}
