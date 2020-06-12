package auctionsniper;

import java.util.Objects;

public class Item {
    public final String identifier;
    public final int stopPrice;

    public Item(String identifier, int stopPrice) {
        this.identifier = identifier;
        this.stopPrice = stopPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return stopPrice == item.stopPrice &&
                Objects.equals(identifier, item.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, stopPrice);
    }

    @Override
    public String toString() {
        return "Item{" +
                "identifier='" + identifier + '\'' +
                ", stopPrice=" + stopPrice +
                '}';
    }
}
