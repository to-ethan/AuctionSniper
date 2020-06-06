package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot snapshot = STARTING_UP;
    private final static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
        this.snapshot = sniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
}