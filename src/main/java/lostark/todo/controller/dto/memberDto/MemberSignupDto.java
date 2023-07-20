package lostark.todo.controller.dto.memberDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MemberSignupDto {

    String username;

    String password;

    String apiKey;

    public MemberSignupDto(String username, String password, String apiKey) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
    }
}
