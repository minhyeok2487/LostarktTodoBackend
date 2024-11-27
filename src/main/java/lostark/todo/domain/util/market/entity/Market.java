package lostark.todo.domain.util.market.entity;

import lombok.*;
import lostark.todo.global.entity.BaseTimeEntity;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Market extends BaseTimeEntity {

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

    public Market changeData(Market news) {
        this.yDayAvgPrice = news.getYDayAvgPrice();
        this.currentMinPrice = news.getCurrentMinPrice();
        this.recentPrice = news.getRecentPrice();
        return this;
    }
}
