package lostark.todo.controller.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupDto {

    String username;

    String password;

    String apiKey;

}
