package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.TimeZone;

public class LocalDateConverterTest {

    @Test
    public void testSupportedType() {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();

        //then
        Assert.assertTrue(factory.supportsType(LocalDate.class));
        Assert.assertFalse(factory.supportsType(ZonedDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalTime.class));
    }

    @Test
    public void testNullValue() throws ValueConverterException {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();
        final Converter converter = factory.getConverterForType(LocalDate.class);

        //when
        final Object rawObject = converter.convertToRemoting(null);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNull(reConverted);
    }

    @Test
    public void testSameTimeZone() throws ValueConverterException {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();
        final LocalDate time = LocalDate.now();
        final Converter converter = factory.getConverterForType(LocalDate.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(LocalDate.class.isAssignableFrom(reConverted.getClass()));
        final LocalDate reconvertedTime = (LocalDate) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

    @Test
    public void testDifferentTimeZone() throws ValueConverterException {
        final TimeZone defaultZone = TimeZone.getDefault();
        try {

            //given
            final LocalDateConverterFactory factory = new LocalDateConverterFactory();
            final LocalDate time = LocalDate.now();
            final Converter converter = factory.getConverterForType(LocalDate.class);
            final TimeZone differentZone = Arrays.asList(TimeZone.getAvailableIDs()).stream()
                    .map(id -> TimeZone.getTimeZone(id))
                    .filter(zone -> !Objects.equals(defaultZone, zone))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No time zone found"));

            //when
            final Object rawObject = converter.convertToRemoting(time);
            TimeZone.setDefault(differentZone);
            final Object reConverted = converter.convertFromRemoting(rawObject);

            //then
            Assert.assertNotNull(rawObject);
            Assert.assertNotNull(reConverted);
            Assert.assertTrue(LocalDate.class.isAssignableFrom(reConverted.getClass()));
            final LocalDate reconvertedTime = (LocalDate) reConverted;
            Assert.assertEquals(reconvertedTime, time);
        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }

}