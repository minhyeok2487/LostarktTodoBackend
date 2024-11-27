package lostark.todo.domain.character.entity;

import lombok.*;
import lostark.todo.domain.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.lang.reflect.Field;

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

   @ColumnDefault("true")
   private boolean showWeekEpona;

   @ColumnDefault("true")
   private boolean showSilmaelChange;

   @ColumnDefault("true")
   private boolean showCubeTicket;

   @ColumnDefault("false")
   private boolean goldCheckVersion;

   @Enumerated(EnumType.STRING)
   @ColumnDefault("'TOP_THREE_POLICY'")
   private GoldCheckPolicyEnum goldCheckPolicyEnum;

   @ColumnDefault("false")
   private boolean linkCubeCal;

   public Settings() {
      this.showCharacter = true;
      this.showEpona = true;
      this.showChaos = true;
      this.showGuardian = true;
      this.showWeekTodo = true;
      this.showWeekEpona = true;
      this.showSilmaelChange = true;
      this.showCubeTicket = true;
      this.goldCheckVersion = false;
      this.goldCheckPolicyEnum = GoldCheckPolicyEnum.TOP_THREE_POLICY;
      this.linkCubeCal = false;
   }

   public void update(String name, boolean value) {
      try {
         Field field = getClass().getDeclaredField(name);
         field.setAccessible(true); // 필드에 접근할 수 있도록 설정
         field.set(this, value);
      } catch (Exception e) {
         throw new IllegalArgumentException("없는 필드 값 입니다.");
      }
   }

   public void updateGoldCheckVersion() {
      this.goldCheckVersion = !goldCheckVersion;
      if (this.goldCheckPolicyEnum.equals(GoldCheckPolicyEnum.RAID_CHECK_POLICY)) {
         this.goldCheckPolicyEnum = GoldCheckPolicyEnum.TOP_THREE_POLICY;
      } else {
         this.goldCheckPolicyEnum = GoldCheckPolicyEnum.RAID_CHECK_POLICY;
      }
   }
}
