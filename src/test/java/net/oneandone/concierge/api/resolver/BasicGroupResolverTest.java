package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.demo.model.DemoData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.testng.Assert.*;

public class BasicGroupResolverTest {

    private GroupResolver groupResolver;

    @BeforeMethod
    public void setUp() {
        groupResolver = new BasicGroupResolver() {

            @Override
            public String[] hierarchy() {
                return new String[]{"tests"};
            }

            @Override
            public int defaultPageSize() {
                return 1;
            }

            @Override
            public int maximumPageSize() {
                return 10;
            }

            @Override
            public Optional<Element> element(Element parent, String address) {
                if ("exists".equals(address)) {
                    return Optional.of(DemoData.USER1);
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public int total(Element parent, Filters filters) {
                if (filters.get(AddressFilter.class).isPresent()) {
                    if ("exists".equals(filters.get(AddressFilter.class).get().getAddress())) {
                        return 1;
                    }
                    return 0;
                } else {
                    return 4;
                }
            }

            @Override
            public ZonedDateTime lastUpdate(Element parent, Filters filters) {
                return ZonedDateTime.of(2016, 4, 16, 10, 49, 0, 0, ZoneId.systemDefault());
            }

            @Override
            public List<Element> elements(Element parent, int page, int perPage, Filters filters) {
                return new ArrayList<>(DemoData.USER_POSTS.keySet());
            }
        };
    }

    @Test
    public void testGetSingleExistingElement() {
        final Group group = groupResolver.elements(null, Filters.Builder.initialize().add(new AddressFilter("exists")).build());
        assertNotNull(group);
        assertEquals(1, group.total());
        assertEquals(1, group.elements().size());
    }

    @Test
    public void testGetSingleMissingElement() {
        final Group group = groupResolver.elements(null, Filters.Builder.initialize().add(new AddressFilter("missing")).build());
        assertNotNull(group);
        assertEquals(0, group.total());
        assertEquals(0, group.elements().size());
    }

    @Test
    public void testGetListOfElements() {
        final Group group = groupResolver.elements(null, Filters.Builder.initialize().build());
        assertNotNull(group);
        assertEquals(4, group.total());
        assertEquals(4, group.elements().size());
    }

    @Test
    public void testGetPagedListOfElements() {
        final Group group = groupResolver.elements(null, Filters.Builder.initialize().add(new PageFilter(1, OptionalInt.empty())).build());
        assertNotNull(group);
        assertEquals(4, group.total());
        assertEquals(4, group.elements().size());
    }


}