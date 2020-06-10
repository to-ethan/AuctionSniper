package integration.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.Main;
import auctionsniper.xmpp.XMPPAuction;
import endtoend.ApplicationRunner;
import endtoend.AuctionSniperEndToEndTest;
import endtoend.FakeAuctionServer;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMPPAuctionTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private XMPPConnection connection;

    @BeforeEach
    public void createConnection() throws XMPPException {
        connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
        connection.connect();
        connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, Main.AUCTION_RESOURCE);
    }

    @BeforeEach
    public void startTheAuction() throws XMPPException {
        auctionServer.startSellingItem();
    }

    @AfterEach
    public void closeConnection() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    @AfterEach
    public void stopAuction() {
        auctionServer.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        Auction auction = new XMPPAuction(connection, auctionServer.getItemId());

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
        };
    }
}