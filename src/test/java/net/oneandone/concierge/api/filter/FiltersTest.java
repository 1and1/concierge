package net.oneandone.concierge.api.filter;

import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

public class FiltersTest {

    @Test
    public void testBuilder() {
        final Filters filters = Filters.Builder.initialize().build();
        assertNotNull(filters, "filters may not be null");
        assertEquals(filters.size(), 0, "filters must be empty");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddingNullFilterToBuilder() {
        Filters.Builder.initialize().add(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*'AddressFilter'.*")
    public void testAddingSameFilterClassToBuilderTwice() {
        final Filter filter = new AddressFilter("id");
        Filters.Builder.initialize().add(filter).add(filter);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGettingFilterForNull() {
        final Filters filters = Filters.Builder.initialize().build();
        filters.get(null);
    }

    @Test
    public void testGettingFilterWithoutResult() {
        final Filters filters = Filters.Builder.initialize().build();
        final Optional<AddressFilter> filter = filters.get(AddressFilter.class);

        assertNotNull(filter, "the optional may not be null");
        assertFalse(filter.isPresent(), "the requested filter may not present");
    }

    @Test
    public void testGettingFilterWithResult() {
        final Filter filter = new AddressFilter("id");
        final Filters filters = Filters.Builder.initialize().add(filter).build();

        final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
        assertTrue(addressFilter.isPresent(), "the address filter must be present");
        assertEquals(addressFilter.get(), filter, "the address filter must be the same as addded");
    }


}