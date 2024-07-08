package com.reactivesprive.movie_info_service.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Validated

@Table(name = "MOVIE_INFO")
public class MovieInfo {
	   @Id
	private String id;
	@NotBlank(message = "movieInfo.name must be present")
	private String name;
	@NotNull
	@Positive(message = "movieInfo.year must be a Positive Value")
	private Integer year;

	@NotNull
	private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
	private LocalDate release_date;
	public MovieInfo(String id, @NotBlank(message = "movieInfo.name must be present") String name,
			@NotNull @Positive(message = "movieInfo.year must be a Positive Value") Integer year,
			@NotNull List<@NotBlank(message = "movieInfo.cast must be present") String> cast, LocalDate release_date) {
		super();
		this.id = id;
		this.name = name;
		this.year = year;
		this.cast = cast;
		this.release_date = release_date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public List<String> getCast() {
		return cast;
	}
	public void setCast(List<String> cast) {
		this.cast = cast;
	}
	public LocalDate getRelease_date() {
		return release_date;
	}
	public void setRelease_date(LocalDate release_date) {
		this.release_date = release_date;
	}
	
}
