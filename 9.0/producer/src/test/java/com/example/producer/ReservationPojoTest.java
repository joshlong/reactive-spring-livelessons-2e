package com.example.producer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReservationPojoTest {



  @Test
  public void create() {
    Reservation reservation = new Reservation("1", "Jane");
    Assert.assertEquals(reservation.getId(), "1");
    Assert.assertEquals(reservation.getName(), "Jane");
    Assert.assertNotNull(reservation.getId());
    Assert.assertThat(reservation.getName(), Matchers.equalToIgnoringCase("Jane"));
    Assert.assertThat(reservation.getName(), new ValidNameMatcher());
  }


}

class ValidNameMatcher extends BaseMatcher<String> {

  @Override
  public boolean matches(Object o) {
    return o instanceof String && isValidName((String) o);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("the name must be non-empty and start with an uppercase letter");
  }

  private boolean isValidName(String name) {
    return name != null && name.length() > 0 && Character.isUpperCase(name.charAt(0));
  }
}
