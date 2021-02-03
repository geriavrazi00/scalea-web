package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.scalea.annotations.FieldsMatch;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@FieldsMatch.List({ 
    @FieldsMatch(
      field = "password", 
      fieldVerify = "confirmPassword", 
      message = "{messages.password.do.not.match}"
    )
})
public class ChangePasswordDTO {
	
	private Long id;
	
	@NotNull
	@Size(max=255, message="{messages.password.max}")
	@NotBlank(message="{messages.password.required}")
	@Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message="{messages.password.format}")
	private String password;
	
	private String confirmPassword;
	
	public ChangePasswordDTO(Long id) {
		this.id = id;
	}
}
