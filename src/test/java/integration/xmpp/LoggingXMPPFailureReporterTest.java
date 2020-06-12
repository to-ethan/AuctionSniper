package integration.xmpp;

import auctionsniper.xmpp.LoggingXMPPFailureReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class LoggingXMPPFailureReporterTest {
    final Logger logger = mock(Logger.class);
    final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterEach
    public void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writeMessageTranslationFailureToLog() {
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));

        verify(logger, times(1)).severe(
                "<auction id> "
                + "Could not translate message \"bad message\" "
                + "because \"java.lang.Exception: bad\"");
    }
}
