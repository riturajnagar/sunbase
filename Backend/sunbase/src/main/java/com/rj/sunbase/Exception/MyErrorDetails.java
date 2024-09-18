package com.rj.sunbase.Exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MyErrorDetails {
	
	private LocalDateTime timestamp;
	private String message;
	private String description;


}
