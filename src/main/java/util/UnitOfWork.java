package util;

import datasource.*;
import domain.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();
    private final List<Object> newObjects = new ArrayList<>();
    private final List<Object> dirtyObjects = new ArrayList<>();
    private final List<Object> deletedObjects = new ArrayList<>();

    public static void newCurrent() {
        setCurrent(new UnitOfWork());
    }

    public static void setCurrent(UnitOfWork uow) {
        current.set(uow);
    }

    public static UnitOfWork getCurrent() {
        return current.get();
    }

    public void registerNew(Object obj) {
        assert !dirtyObjects.contains(obj);
        assert !deletedObjects.contains(obj);
        newObjects.add(obj);
    }

    public void registerDirty(Object obj) {
        assert !deletedObjects.contains(obj);
        if (!dirtyObjects.contains(obj)) {
            dirtyObjects.add(obj);
        }
    }

    public void registerDeleted(Object obj) {
        if (newObjects.remove(obj)) return;
        dirtyObjects.remove(obj);
        if (!deletedObjects.contains(obj)) {
            deletedObjects.add(obj);
        }
    }

    public void commit() throws Exception {
        Connection conn = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false);
            for (Object obj : newObjects) {
                if (obj instanceof ShippingDetails) {
                    ShippingDetailsMapper.insert((ShippingDetails) obj, conn);
                } else if (obj instanceof Order) {
                    OrderMapper.insert((Order) obj, conn);
                } else if (obj instanceof Listing) {
                    Listing listing = (Listing) obj;
                    ListingMapper.insert(listing, conn);
                    for (StandardUser coSeller : listing.getCoSellers()) {
                        ListingMapper.insertCoSeller(coSeller.getId(), listing.getId(), conn);
                    }
                } else {
                    throw new Exception("Unexpected object of type " + obj.getClass().getName() + " in UOW new list");
                }
            }
            for (Object obj : dirtyObjects) {
                if (obj instanceof Listing) {
                    Listing listing = (Listing) obj;
                    Listing oldListing = ListingMapper.fetch(listing.getId(), false, conn);
                    for (StandardUser coSeller : listing.getCoSellers()) {
                        if (oldListing.getCoSellers().stream().noneMatch(o -> coSeller.getId().equals(o.getId()))) {
                            ListingMapper.insertCoSeller(coSeller.getId(), listing.getId(), conn);
                        }
                    }
                    for (StandardUser coSeller : oldListing.getCoSellers()) {
                        if (listing.getCoSellers().stream().noneMatch(o -> coSeller.getId().equals(o.getId()))) {
                            ListingMapper.deleteCoSeller(coSeller.getId(), listing.getId(), conn);
                        }
                    }
                    ListingMapper.update(listing, conn);
                } else if (obj instanceof ShippingDetails) {
                    ShippingDetailsMapper.update((ShippingDetails) obj, conn);
                } else if (obj instanceof Order) {
                    OrderMapper.update((Order) obj, conn);
                } else {
                    throw new Exception("Unexpected object of type " + obj.getClass().getName() + " in UOW dirty list");
                }
            }
            for (Object obj : deletedObjects) {
                if (obj instanceof StandardUser) {
                    StandardUser user = (StandardUser) UserMapper.fetch(((User) obj).getId(), true, conn);
                    user.setActive(false);
                    UserMapper.update(user, conn);
                } else if (obj instanceof Listing) {
                    Listing listing = ListingMapper.fetch(((Listing) obj).getId(), true, conn);
                    listing.setActive(false);
                    ListingMapper.update(listing, conn);
                } else if (obj instanceof Order) {
                    Order order = OrderMapper.fetch(((Order) obj).getId(), true, conn);
                    order.setCancelled(true);
                    OrderMapper.update(order, conn);
                } else {
                    throw new Exception("Unexpected object of type " + obj.getClass().getName() + " in UOW deleted list");
                }
            }
            conn.commit();
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    // System.out.println("Transaction has been rolled back");
                }
            } catch (SQLException excep) {
                // System.out.println(excep.getMessage());
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // System.out.println(e.getMessage());
            }
        }
    }
}
