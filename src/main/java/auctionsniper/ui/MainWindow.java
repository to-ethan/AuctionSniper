package auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join button";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfilio) {
        SnipersTableModel model = new SnipersTableModel();
        portfilio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        stopPriceField.setColumns(7);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
            }
            private String itemId() {
                return itemIdField.getText();
            }
            private int stopPrice() {
                return ((Number) stopPriceField.getValue()).intValue();
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }
}
