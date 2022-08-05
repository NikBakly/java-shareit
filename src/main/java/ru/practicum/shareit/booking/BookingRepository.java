package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.status.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByEndDesc(Long bookedId);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(Long bookerId, Status status);

    @Query("select b from Booking as b" +
            " join Item as i on i.id = b.itemId" +
            " where i.ownerId = :ownerId" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerIdOrderByEndDesc(@Param("ownerId") Long ownerId);

    @Query("select b from Booking as b" +
            " join Item as i on i.id = b.itemId" +
            " where i.ownerId = :ownerId and b.status = :status" +
            " order by b.end desc ")
    List<Booking> findAllByOwnerIdAndStatusOrderByEndDesc(@Param("ownerId") Long ownerId, @Param("status") Status status);

    @Query(value = "SELECT * from BOOKINGS b " +
            " JOIN ITEMS I ON I.ID = b.ITEM_ID" +
            " WHERE I.ID = :itemId AND I.OWNER_ID =:ownerId AND b.STATUS = 'APPROVED'" +
            " ORDER BY b.END_DATE_TIME " +
            " LIMIT 2",
            nativeQuery = true)
    List<Booking> findTwoBookingByOwnerIdOrderByEndAsc(@Param("ownerId") Long ownerId, @Param("itemId") Long itemId);


    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findAllByItemId(Long itemId);
}
