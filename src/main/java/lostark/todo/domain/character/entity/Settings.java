package lostark.todo.domain.character.entity;

import lombok.*;
import lostark.todo.domain.character.enums.goldCheckPolicy.GoldCheckPolicyEnum;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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

   @ColumnDefault("0")
   private int thresholdEpona;

   @ColumnDefault("true")
   private boolean showChaos;

   @ColumnDefault("0")
   private int thresholdChaos;

   @ColumnDefault("true")
   private boolean showGuardian;

   @ColumnDefault("0")
   private int thresholdGuardian;

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

   @ColumnDefault("true")
   private boolean showMoreButton;

   public Settings() {
      this.showCharacter = true;
      this.showEpona = true;
      this.thresholdEpona = 0;
      this.showChaos = true;
      this.thresholdChaos = 0;
      this.showGuardian = true;
      this.thresholdGuardian = 0;
      this.showWeekTodo = true;
      this.showWeekEpona = true;
      this.showSilmaelChange = true;
      this.showCubeTicket = true;
      this.goldCheckVersion = false;
      this.goldCheckPolicyEnum = GoldCheckPolicyEnum.TOP_THREE_POLICY;
      this.linkCubeCal = false;
      this.showMoreButton = true;
   }

   public void update(String name, Object value) {
      try {
         Field field = getClass().getDeclaredField(name);
         field.setAccessible(true); // 필드에 접근할 수 있도록 설정
         field.set(this, value);
      } catch (Exception e) {
         throw new ConditionNotMetException("없는 필드 값 입니다.");
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
