package com.App.fullStack.pojos;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; 

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "user") 
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User { 

	@Id
	private String id; 
	private String fullName; 
	private String email; 
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 
	private String password;
} 
