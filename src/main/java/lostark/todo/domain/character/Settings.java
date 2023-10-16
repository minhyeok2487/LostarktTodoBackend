package lostark.todo.domain.character;

import lombok.*;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.domain.content.DayContent;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Settings {

   @ColumnDefault("true")
   private boolean showCharacter;

   @ColumnDefault("true")
   private boolean showEpona;

   @ColumnDefault("true")
   private boolean showChaos;

   @ColumnDefault("true")
   private boolean showGuardian;

   @ColumnDefault("true")
   private boolean showWeekTodo;

   public Settings() {
      this.showCharacter = true;
      this.showEpona = true;
      this.showChaos = true;
      this.showGuardian = true;
      this.showWeekTodo = true;
   }

//   public void update(String name, boolean value) {
//      if(name.equals("showCharacter")) {
//         this.showCharacter = value;
//      }
//      if(name.equals("showEpona")) {
//         this.showEpona = value;
//      }
//      if(name.equals("showChaos")) {
//         this.showChaos = value;
//      }
//      if(name.equals("showGuardian")) {
//         this.showGuardian = value;
//      }
//      if(name.equals("showWeekTodo")) {
//         this.showWeekTodo = value;
//      }
//   }

   public void update(String name, boolean value) {
      try {
         Field field = getClass().getDeclaredField(name);
         field.setAccessible(true); // 필드에 접근할 수 있도록 설정
         field.set(this, value);
      } catch (Exception e) {
         throw new IllegalArgumentException("없는 필드 값 입니다.");
      }
   }
}
