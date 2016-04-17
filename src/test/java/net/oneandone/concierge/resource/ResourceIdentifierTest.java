package net.oneandone.concierge.resource;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ResourceIdentifierTest {
    @Test
    public void testParse() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");
        assertNotNull(identifier);
        assertEquals(new String[] { "a", "b", "c" }, identifier.getIdentifier());
    }

    @Test
    public void testGetResolverPath() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");
        assertEquals(new String[] { "a", "c" }, identifier.getResolverHierarchy());
    }

    @Test
    public void testIsFinalPart() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");

        assertFalse(identifier.isFinalPart());
        assertTrue(identifier.hasNextPart());
        assertTrue(identifier.getNextPart().isFinalPart());
    }

    @Test
    public void testHasNextPart() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");

        assertTrue(identifier.hasNextPart());
        final ResourceIdentifier nextPart = identifier.getNextPart();

        assertEquals(new String[]{ "c" }, nextPart.getIdentifier());
        assertEquals(new String[] { "a", "b", "c" }, identifier.getCompleteIdentifier());
    }

    @Test
    public void testGetNextPart() throws Exception {
        assertTrue(ResourceIdentifier.parse("a/b/c").hasNextPart());
        assertFalse(ResourceIdentifier.parse("a").hasNextPart());
        assertFalse(ResourceIdentifier.parse("a/b").hasNextPart());
    }

    @Test
    public void testIsElementIdentifier() throws Exception {
        assertTrue(ResourceIdentifier.parse("a/b/c").isElementIdentifier());
        assertFalse(ResourceIdentifier.parse("a").isElementIdentifier());
        assertTrue(ResourceIdentifier.parse("a/b").isElementIdentifier());
        assertTrue(ResourceIdentifier.parse("a/b/c/d").isElementIdentifier());
    }

    @Test
    public void testGetGroupOrExtensionIdentifier() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b");
        assertEquals("a", identifier.getGroupOrExtensionIdentifier());
    }

    @Test
    public void testGetElementIdentifier() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b");
        assertEquals("b", identifier.getElementIdentifier());
    }

}