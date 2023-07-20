package lostark.todo.domain.market;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private long id;

    private long lostarkMarketId;

    private double yDayAvgPrice;

    private int currentMinPrice;

    private String grade;

    private int recentPrice;

    private String icon;

    private String name;

    private int bundleCount;

    private int categoryCode;

    // Lostark Market Api로 불러온 JSONObject Constructor
    // 카테고리 코드도 있어야함
    public Market(JSONObject tempJson, int categoryCode) {
        this.lostarkMarketId = Long.parseLong(tempJson.get("Id").toString());
        this.yDayAvgPrice = Double.parseDouble(tempJson.get("YDayAvgPrice").toString());
        this.currentMinPrice = Integer.parseInt(tempJson.get("CurrentMinPrice").toString());
        this.grade = tempJson.get("Grade").toString();
        this.recentPrice = Integer.parseInt(tempJson.get("RecentPrice").toString());
        this.icon = tempJson.get("Icon").toString();
        this.name = tempJson.get("Name").toString();
        this.bundleCount = Integer.parseInt(tempJson.get("BundleCount").toString());
        this.categoryCode = categoryCode;
    }


    public static Market createAuctionItem(JSONObject item, String itemName, int categoryCode) {
        JSONObject actionInfo = (JSONObject) item.get("AuctionInfo");
        Market market = new Market();
        market.lostarkMarketId = 0;
        market.yDayAvgPrice = 0;
        market.currentMinPrice = Integer.parseInt(actionInfo.get("BuyPrice").toString());
        market.recentPrice = Integer.parseInt(actionInfo.get("BuyPrice").toString());
        market.grade = item.get("Grade").toString();
        market.icon = item.get("Icon").toString();
        market.name = itemName;
        market.bundleCount = 1;
        market.categoryCode = categoryCode;
        return market;
    }
}
