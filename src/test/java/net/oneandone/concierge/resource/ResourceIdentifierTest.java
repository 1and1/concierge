package net.oneandone.concierge.resource;

import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ResourceIdentifierTest {

    @Test
    public void testEmptyResourceIdentifier() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("");
        assertNotNull(identifier);
        assertTrue(identifier.empty());
    }

    @Test
    public void testResourceIdentifierWithOneGroup() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("group/");
        assertNotNull(identifier);
        assertFalse(identifier.empty());
        assertEquals(new String[]{"group"}, identifier.get());
        assertEquals(new String[]{"group"}, identifier.hierarchy());
        assertEquals(new String[]{"group"}, identifier.completeHierarchy());
        assertEquals("group", identifier.groupIdentifier());
        assertTrue(identifier.extensions().isEmpty());
        assertFalse(identifier.hasNextScope());
        assertFalse(identifier.hasElementIdentifier());

        final Filters filters = identifier.filters();
        assertFalse(filters.get(AddressFilter.class).isPresent());
        assertFalse(filters.get(PageFilter.class).isPresent());
    }

    @Test
    public void testResourceIdentifierWithOneGroupsAndAnElement() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("group1/id1");
        assertNotNull(identifier);
        assertFalse(identifier.empty());
        assertEquals(new String[]{"group1", "id1"}, identifier.get());
        assertEquals(new String[]{"group1"}, identifier.hierarchy());
        assertEquals(new String[]{"group1"}, identifier.completeHierarchy());
        assertEquals("group1", identifier.groupIdentifier());
        assertTrue(identifier.extensions().isEmpty());
        assertFalse(identifier.hasNextScope());
        assertTrue(identifier.hasElementIdentifier());

        final Filters filters = identifier.filters();
        assertTrue(filters.get(AddressFilter.class).isPresent());
        assertEquals("id1", filters.get(AddressFilter.class).get().getAddress());
        assertFalse(filters.get(PageFilter.class).isPresent());
    }

    @Test
    public void testResourceIdentifierWithTwoGroupsAndElements() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("group1/id1/group2/id2");
        assertNotNull(identifier);
        assertFalse(identifier.empty());
        assertEquals(new String[]{"group1", "id1", "group2", "id2"}, identifier.get());
        assertEquals(new String[]{"group1"}, identifier.hierarchy());
        assertEquals(new String[]{"group1", "group2"}, identifier.completeHierarchy());
        assertEquals("group1", identifier.groupIdentifier());
        assertTrue(identifier.extensions().isEmpty());
        assertTrue(identifier.hasNextScope());
        assertTrue(identifier.hasElementIdentifier());

        final Filters filters = identifier.filters();
        assertTrue(filters.get(AddressFilter.class).isPresent());
        assertEquals("id1", filters.get(AddressFilter.class).get().getAddress());
        assertFalse(filters.get(PageFilter.class).isPresent());

        final ResourceIdentifier nextScope = identifier.next();
        assertEquals(new String[]{"group1", "group2"}, nextScope.hierarchy());
        assertEquals("group2", nextScope.groupIdentifier());
        assertFalse(nextScope.hasNextScope());
        assertTrue(nextScope.hasElementIdentifier());

        final Filters nextScopeFilters = nextScope.filters();

        assertTrue(nextScopeFilters.get(AddressFilter.class).isPresent());
        assertEquals("id2", nextScopeFilters.get(AddressFilter.class).get().getAddress());
        assertFalse(nextScopeFilters.get(PageFilter.class).isPresent());
    }

    @Test
    public void testResourceIdentifierWithTwoGroupsAndElementsWithExtension() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("group1/id1/group2/id2/extension");
        assertNotNull(identifier);
        assertFalse(identifier.empty());
        assertEquals(new String[]{"group1", "id1", "group2", "id2", "extension"}, identifier.get());
        assertEquals(new String[]{"group1"}, identifier.hierarchy());
        assertEquals(new String[]{"group1", "group2", "extension"}, identifier.completeHierarchy());
        assertEquals("group1", identifier.groupIdentifier());
        assertTrue(identifier.extensions().isEmpty());
        assertTrue(identifier.hasNextScope());
        assertTrue(identifier.hasElementIdentifier());

        final Filters filters = identifier.filters();
        assertTrue(filters.get(AddressFilter.class).isPresent());
        assertEquals("id1", filters.get(AddressFilter.class).get().getAddress());
        assertFalse(filters.get(PageFilter.class).isPresent());

        final ResourceIdentifier nextScope = identifier.next();
        assertEquals(new String[]{"group1", "group2"}, nextScope.hierarchy());
        assertEquals("group2", nextScope.groupIdentifier());
        assertTrue(nextScope.hasNextScope());
        assertTrue(nextScope.hasElementIdentifier());

        final Filters nextScopeFilters = nextScope.filters();

        assertTrue(nextScopeFilters.get(AddressFilter.class).isPresent());
        assertEquals("id2", nextScopeFilters.get(AddressFilter.class).get().getAddress());
        assertFalse(nextScopeFilters.get(PageFilter.class).isPresent());

        final ResourceIdentifier extensionScope = nextScope.next();
        assertEquals(new String[]{"group1", "group2", "extension"}, extensionScope.hierarchy());
        assertEquals("extension", extensionScope.groupIdentifier());
        assertFalse(extensionScope.hasNextScope());
        assertFalse(extensionScope.hasElementIdentifier());
    }
}