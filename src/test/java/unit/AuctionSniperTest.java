package unit;

import auctionsniper.*;
import org.junit.jupiter.api.Test;

import static endtoend.AuctionSniperEndToEndTest.ITEM_ID;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private final Auction auction = mock(Auction.class);
    private final SniperListener sniperListener = mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperLost();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        // TODO: Validate bidding state of sniper (p. 145)
        verify(sniperListener, atLeastOnce()).sniperLost();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperWon();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);

        verify(auction, times(1)).bid(price + increment);
        verify(sniperListener, atLeast(1)).sniperBidding(new SniperState(ITEM_ID, price, bid));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);

        verify(sniperListener, atLeastOnce()).sniperWinning();
    }


}
