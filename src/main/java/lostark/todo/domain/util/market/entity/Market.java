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

    public void changeData(Market news) {
        this.yDayAvgPrice = news.getYDayAvgPrice();
        this.currentMinPrice = news.getCurrentMinPrice();
        this.recentPrice = news.getRecentPrice();
    }

    public void updatePrice(JSONObject jsonObject) {
        JSONObject auctionInfo = (JSONObject) jsonObject.get("AuctionInfo");
        if (auctionInfo == null || !auctionInfo.containsKey("BuyPrice")) {
            throw new IllegalArgumentException("Invalid AuctionInfo data");
        }

        int price = calculatePrice(auctionInfo.get("BuyPrice").toString());
        this.yDayAvgPrice = price;
        this.currentMinPrice = price;
        this.recentPrice = price;
    }

    private int calculatePrice(String buyPrice) {
        final int CONVERSION_RATE = 81;
        return Integer.parseInt(buyPrice) / CONVERSION_RATE;
    }

}
