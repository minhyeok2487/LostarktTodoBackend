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
import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

   public void update(String name, boolean value) {
      if(name.equals("showCharacter")) {
         this.showCharacter = value;
      }
      if(name.equals("showEpona")) {
         this.showEpona = value;
      }
      if(name.equals("showChaos")) {
         this.showChaos = value;
      }
      if(name.equals("showGuardian")) {
         this.showGuardian = value;
      }
      if(name.equals("showWeekTodo")) {
         this.showWeekTodo = value;
      }
   }
}
