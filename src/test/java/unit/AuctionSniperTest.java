package unit;

import auctionsniper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static auctionsniper.SniperState.*;
import static endtoend.AuctionSniperEndToEndTest.ITEM_ID;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private final Auction auction = mock(Auction.class);
    private final SniperListener sniperListener = mock(SniperListener.class);
    private AuctionSniper sniper;
    private final Item item = new Item(ITEM_ID, 1234);

    @BeforeEach
    public void initializeSniper() {
        sniper = new AuctionSniper(item, auction);
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 0, 0, LOST));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        // TODO: Validate bidding state of sniper (p. 145)
        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123,168, LOST));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 0, SniperState.WON));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction, times(1)).bid(price + increment);

        verify(sniperListener, atLeast(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);

        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentBidExceedsStopPrice() {
        int bid = 123 + 45;
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.currentPrice(2345, 23, FromOtherBidder);

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        sniper.currentPrice(1235, 45, FromOtherBidder);

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 1235, 0, LOSING));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        int bid = 1235;

        sniper.currentPrice(bid, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, bid, 0, LOSING));

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, bid, 0, LOST));
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        sniper.currentPrice(1235, 45, FromOtherBidder);
        sniper.currentPrice(1280, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 1235, 0, LOSING));

        verify(sniperListener, times(1)).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 1280, 0, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        sniper.currentPrice(123, 45, FromSniper);
        sniper.currentPrice(2000, 45, FromOtherBidder);

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 0, WINNING));

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 2000, 0, LOSING));
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() {
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionFailed();

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 0,0,FAILED));
    }
}
