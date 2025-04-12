package ru.practicum.shareit.extention;

public class ExceptionMessages {
    public static final String USER_NOT_FOUND_ERROR = "Пользователь с id = %d не найден";
    public static final String ITEM_NOT_FOUND_ERROR = "Вешь с id = %d не найдена";
    public static final String BOOKING_NOT_FOUND_ERROR = "Бронирование с id = %d не найдено";
    public static final String BOOKING_CHANGE_STATUS = "Только владелец предмета может менять статус бронирования";
    public static final String BOOKING_ITEM_IS_BOOKED = "Предмет уже забронирован на это время";
    public static final String BOOKING_ITEM_IS_NOT_AVAILABLE = "Предмет недоступен для аренды";
    public static final String BOOKING_OWNER_CANT_BOOKED = "Владелец предмета не может арендовать его сам";
    public static final String BOOKING_END_BEFORE_START = "Время окончания бронирования должно быть после времени начала";
    public static final String BOOKING_GET_INFO_ERROR = "Только владелец ил бронирующий может получить информацию о бронировании";
    public static final String NOT_FOUND_ITEM = "Предмет не найден";
    public static final String NOT_FOUND_USER = "Пользователь не найден";
    public static final String NOT_WAS_RENT = "Пользователь не арендовал предмет или время аренды еще не вышло";
}
