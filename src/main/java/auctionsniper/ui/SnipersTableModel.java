package auctionsniper.ui;

import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

import static auctionsniper.ui.MainWindow.STATUS_JOINING;

public class SnipersTableModel extends AbstractTableModel {
    private final SniperState STARTING_UP = new SniperState("", 0, 0);
    private SniperState sniperState = STARTING_UP;
    private String statusText = STATUS_JOINING;

    public SnipersTableModel() { }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return sniperState.itemId;
            case LAST_PRICE:
                return sniperState.lastPrice;
            case LAST_BID:
                return sniperState.lastBid;
            case SNIPER_STATUS:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState sniperState, String statusText) {
        this.sniperState = sniperState;
        this.statusText = statusText;
        fireTableRowsUpdated(0, 0);
    }
}