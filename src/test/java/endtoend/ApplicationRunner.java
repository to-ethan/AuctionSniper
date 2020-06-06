package endtoend;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static auctionsniper.ui.MainWindow.*;
import static auctionsniper.ui.SnipersTableModel.textFor;
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
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showSniperStatus("", 0, 0, textFor(SniperState.JOINING));
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showSniperStatus(itemId, lastPrice, lastBid, STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showSniperStatus(itemId, winningBid, winningBid, STATUS_WINNING);
    }

    public void showSniperHasLostAuction(int lastPrice, int lastBid) {
        driver.showSniperStatus(itemId, lastPrice, lastBid, STATUS_LOST);
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
