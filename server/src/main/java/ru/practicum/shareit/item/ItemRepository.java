package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    //Метод для поиска предмета по тексту
    @Query("select it from Item as it" +
            " where (upper(it.name) like concat('%', upper(:text), '%' ) " +
            " or upper(it.description) like concat('%', upper(:text), '%' ))" +
            " and it.available = true")
    Page<Item> findItemsByText(@Param("text") String text, Pageable pageable);


    @Query("select it from Item as it" +
            " where (upper(it.name) like concat('%', upper(:text), '%' ) " +
            " or upper(it.description) like concat('%', upper(:text), '%' ))" +
            " and it.available = true")
    List<Item> findItemsByText(@Param("text") String text);

    //Метод для поиска предметов по id владельца
    Page<Item> findAllByOwnerId(Long userId, Pageable pageable);

    List<Item> findAllByOwnerId(Long userId);


    //Метод для поиска предмета по id запроса
    List<Item> findAllByRequestId(Long requestId);
}
