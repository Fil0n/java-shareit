package ru.practicum.shareit.booking.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.practicum.shareit.booking.model.BookingEvent;
import ru.practicum.shareit.booking.model.BookingStatusType;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class BookingStateMachineConfig extends EnumStateMachineConfigurerAdapter<BookingStatusType, BookingEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<BookingStatusType, BookingEvent> states) throws Exception {
        states
                .withStates()
                .initial(BookingStatusType.WAITING)
                .states(EnumSet.allOf(BookingStatusType.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BookingStatusType, BookingEvent> transitions) throws Exception {
        transitions
                .withExternal() // WAITING → APPROVED
                .source(BookingStatusType.WAITING)
                .target(BookingStatusType.APPROVED)
                .event(BookingEvent.APPROVE)

                .and()
                .withExternal() // WAITING → REJECTED
                .source(BookingStatusType.WAITING)
                .target(BookingStatusType.REJECTED)
                .event(BookingEvent.REJECT)

                .and()
                .withExternal() // WAITING → CANCELLED
                .source(BookingStatusType.WAITING)
                .target(BookingStatusType.CANCELLED)
                .event(BookingEvent.CANCEL);
    }
}
