package com.example.reservationservice;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ReservationPojoTest {

  @Test
  public void create() {
    Reservation reservation = new Reservation("1", "Name");
    Assert.assertEquals(reservation.getId(), "1");
    Assert.assertEquals(reservation.getName(), "Name");
    Assert.assertThat(reservation.getName(), Matchers.equalToIgnoringCase("Name"));

  }
}
