package auctionsniper;

import java.util.Objects;

import static auctionsniper.SniperState.*;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState sniperState) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = sniperState;
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0,0, JOINING);
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, BIDDING);
    }

    public SniperSnapshot losing(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, LOSING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, WINNING);
    }

    public SniperSnapshot failed() {
        return new SniperSnapshot(itemId, 0,0, FAILED);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SniperSnapshot)) return false;
        SniperSnapshot that = (SniperSnapshot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid, state);
    }

    public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
        return itemId.equals(sniperSnapshot.itemId);
    }
}
