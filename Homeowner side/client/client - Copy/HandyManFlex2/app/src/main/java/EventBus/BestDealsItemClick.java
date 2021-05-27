package EventBus;

import com.example.handymanflex.Model.BestDealModel;

public class BestDealsItemClick {
    private BestDealModel bestDealModel;

    public BestDealsItemClick(BestDealModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }

    public BestDealModel getBestDealModel() {
        return bestDealModel;
    }

    public void setBestDealModel(BestDealModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }
}
