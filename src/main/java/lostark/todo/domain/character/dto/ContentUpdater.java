package lostark.todo.domain.character.dto;

import lombok.Getter;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class ContentUpdater {
    private final Supplier<Integer> beforeGaugeSupplier;  // 이전 진행도를 제공하는 Supplier
    private final Supplier<Integer> thresholdSupplier;    // 설정된 임계값을 제공하는 Supplier
    private final Supplier<Boolean> isDisplayedSupplier; // 해당 컨텐츠가 표시되는지 여부를 제공하는 Supplier
    private final Supplier<Integer> getCheckValue;       // 현재 체크된 값을 제공하는 Supplier
    private final Consumer<Integer> setCheckValue;       // 체크된 값을 업데이트하는 Consumer
    private final Runnable updateMethod;                 // 업데이트 메서드를 실행하는 Runnable
    private final int completedValue;                    // 완료 상태의 값 (1, 2, 3 등)

    /**
     * DayTodo와 Settings를 기반으로 ContentUpdater 리스트를 생성
     */
    public static List<ContentUpdater> toDto(DayTodo dayTodo, Settings settings) {
        return List.of(
                new ContentUpdater(dayTodo::getBeforeEponaGauge, settings::getThresholdEpona,
                        settings::isShowEpona, dayTodo::getEponaCheck2, dayTodo::setEponaCheck2,
                        dayTodo::updateCheckEponaAll, 3),
                new ContentUpdater(dayTodo::getBeforeChaosGauge, settings::getThresholdChaos,
                        settings::isShowChaos, dayTodo::getChaosCheck, dayTodo::setChaosCheck,
                        dayTodo::updateCheckChaos, 2),
                new ContentUpdater(dayTodo::getBeforeGuardianGauge, settings::getThresholdGuardian,
                        settings::isShowGuardian, dayTodo::getGuardianCheck, dayTodo::setGuardianCheck,
                        dayTodo::updateCheckGuardian, 1)
        );
    }

    /**
     * ContentUpdater 생성자
     */
    public ContentUpdater(Supplier<Integer> beforeGaugeSupplier, Supplier<Integer> thresholdSupplier, Supplier<Boolean> isDisplayedSupplier,
                          Supplier<Integer> getCheckValue, Consumer<Integer> setCheckValue, Runnable updateMethod, int completedValue) {
        this.beforeGaugeSupplier = beforeGaugeSupplier;
        this.thresholdSupplier = thresholdSupplier;
        this.isDisplayedSupplier = isDisplayedSupplier;
        this.getCheckValue = getCheckValue;
        this.setCheckValue = setCheckValue;
        this.updateMethod = updateMethod;
        this.completedValue = completedValue;
    }

    /**
     * 컨텐츠가 표시될 조건을 만족하는지 확인
     * - isDisplayedSupplier가 true이고, beforeGauge가 threshold 이상이면 표시
     */
    public boolean isDisplayed() {
        return isDisplayedSupplier.get() && beforeGaugeSupplier.get() >= thresholdSupplier.get();
    }

    /**
     * 현재 체크 상태가 완료 상태인지 확인
     */
    public boolean isChecked() {
        return getCheckValue.get() == completedValue;
    }

    /**
     * 체크 상태 업데이트
     * @param value 새로운 체크 값 (0: 해제, 1/2/3: 완료 값)
     */
    public void updateCheck(int value) {
        setCheckValue.accept(value);
    }

    /**
     * 업데이트 메서드 실행 (DB 반영 등)
     */
    public void runUpdateMethod() {
        updateMethod.run();
    }
}

