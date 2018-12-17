package telran.ashkelon2018.forum.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class DatePeriodDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate fromDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate toDate;
}
