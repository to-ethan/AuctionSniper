package auctionsniper.ui;

import auctionsniper.*;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
    private final static String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};
    private ArrayList<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));

    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        int row = snapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return snapshots.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + newSnapshot);
    }

    public class SwingThreadSniperListener implements SniperListener {
        private SnipersTableModel snipers;

        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
            snipers.sniperStateChanged(sniperSnapshot);
        }
    }

}