package com.reactivesprive.movie_info_service.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.reactivesprive.movie_info_service.domain.MovieInfo;

@Repository
@Service
public interface MovieInfoRepository extends ReactiveCrudRepository<MovieInfo, String> {

}
