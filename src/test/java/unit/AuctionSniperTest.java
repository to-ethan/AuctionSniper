package unit;

import auctionsniper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static auctionsniper.SniperState.WINNING;
import static endtoend.AuctionSniperEndToEndTest.ITEM_ID;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private final Auction auction = mock(Auction.class);
    private final SniperListener sniperListener = mock(SniperListener.class);
    private AuctionSniper sniper;

    @BeforeEach
    public void initializeSniper() {
        sniper = new AuctionSniper(ITEM_ID, auction);
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        // TODO: Validate bidding state of sniper (p. 145)
        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123,168, SniperState.LOST));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WON));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);

        verify(auction, times(1)).bid(price + increment);
        verify(sniperListener, atLeast(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, AuctionEventListener.PriceSource.FromSniper);

        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
    }


}
