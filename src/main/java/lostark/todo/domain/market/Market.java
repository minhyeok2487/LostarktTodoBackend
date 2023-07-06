package lostark.todo.domain.market;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Market {

    @Id
    @Column(name = "market_id")
    private long id;

    private double yDayAvgPrice;

    private int currentMinPrice;

    private String grade;

    private int recentPrice;

    private String icon;

    private String name;

    private int bundleCount;

    private int categoryCode;

    // Lostark Api로 불러온 JSONObject Constructor
    // 카테고리 코드도 있어야함
    public Market(JSONObject tempJson, int categoryCode) {
        this.id = Long.parseLong(tempJson.get("Id").toString());
        this.yDayAvgPrice = Double.parseDouble(tempJson.get("YDayAvgPrice").toString());
        this.currentMinPrice = Integer.parseInt(tempJson.get("CurrentMinPrice").toString());
        this.grade = tempJson.get("Grade").toString();
        this.recentPrice = Integer.parseInt(tempJson.get("RecentPrice").toString());
        this.icon = tempJson.get("Icon").toString();
        this.name = tempJson.get("Name").toString();
        this.bundleCount = Integer.parseInt(tempJson.get("BundleCount").toString());
        this.categoryCode = categoryCode;
    }
}
