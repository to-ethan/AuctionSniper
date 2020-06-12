package integration.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.Item;
import auctionsniper.xmpp.XMPPAuctionException;
import auctionsniper.xmpp.XMPPAuctionHouse;
import endtoend.AuctionSniperEndToEndTest;
import endtoend.FakeAuctionServer;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static endtoend.ApplicationRunner.SNIPER_ID;
import static endtoend.ApplicationRunner.SNIPER_PASSWORD;
import static endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMPPAuctionHouseTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private XMPPAuctionHouse auctionHouse;

    @BeforeEach
    public void createConnection() throws XMPPException, XMPPAuctionException {
        auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
    }

    @BeforeEach
    public void startTheAuction() throws XMPPException {
        auctionServer.startSellingItem();
    }

    @AfterEach
    public void closeConnection() {
        if (auctionHouse != null) {
            auctionHouse.disconnect();
        }
    }

    @AfterEach
    public void stopAuction() {
        auctionServer.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        Auction auction = auctionHouse.auctionFor(new Item(auctionServer.getItemId(), Integer.MAX_VALUE));

        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
        auction.join();

        auctionServer.hasReceivedJoinRequestFromSniper(AuctionSniperEndToEndTest.SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS), "should have been closed");
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // not implemented
            }

            @Override
            public void auctionFailed() {
                // not implemented
            }
        };
    }
}