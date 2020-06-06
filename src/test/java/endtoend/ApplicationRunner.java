package endtoend;

import auctionsniper.Main;

import static auctionsniper.ui.MainWindow.*;
import static endtoend.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;
    private String itemId;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();

        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(new String[]{
                            XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId()
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        //driver.showSniperStatus(itemId, 1000, 1000, STATUS_JOINING);
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showSniperStatus(itemId, lastPrice, lastBid, STATUS_BIDDING);
    }

    public void showSniperHasLostAuction(int winningBid) {
        driver.showSniperStatus(itemId, winningBid, winningBid, STATUS_LOST);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showSniperStatus(itemId, winningBid, winningBid, STATUS_WINNING);
    }

    public void showSniperHasWonAuction(int lastPrice) {
        driver.showSniperStatus(itemId, lastPrice, lastPrice, STATUS_WON);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
