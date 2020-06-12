package unit;

import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static endtoend.ApplicationRunner.SNIPER_ID;
import static org.mockito.Mockito.*;

public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener = mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);

    private AuctionMessageTranslator translator;

    @BeforeEach
    public void initializeTranslator() {
        translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);
    }

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1))
                .currentPrice(192, 7, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody(
                "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1))
                .currentPrice(234, 5, AuctionEventListener.PriceSource.FromSniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        String badMessage = "bad message";


        translator.processMessage(UNUSED_CHAT, message(badMessage));

        expectFailureWithMessage(badMessage);
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        String badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        expectFailureWithMessage(badMessage);
    }

    private void expectFailureWithMessage(String badMessage) {
        verify(listener, times(1)).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(
                eq(SNIPER_ID), eq(badMessage), any(Exception.class));
    }

    private Message message(String badMessage) {
        Message message = new Message();
        message.setBody(badMessage);
        return message;
    }
}
