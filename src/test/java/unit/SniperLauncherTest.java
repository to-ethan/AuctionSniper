package unit;

import auctionsniper.*;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class SniperLauncherTest {

    private final Auction auction = mock(Auction.class);
    private final AuctionHouse auctionHouse = mock(AuctionHouse.class);
    private final SniperCollector collector = mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, collector);

    @Test
    public void addsNewSniperToCollectorThenJoinsAuction() {
        final Item item = new Item("item 123", 456);
        when(auctionHouse.auctionFor(item)).thenReturn(auction);
        InOrder orderVerifier = inOrder(auction, collector, auction);

        launcher.joinAuction(item);

        // TODO: implement with(sniperForItem(item)) instead of any(AuctionSniper.class)
        orderVerifier.verify(auction).addAuctionEventListener(any(AuctionSniper.class));
        orderVerifier.verify(collector).addSniper(any(AuctionSniper.class));
    }

}
