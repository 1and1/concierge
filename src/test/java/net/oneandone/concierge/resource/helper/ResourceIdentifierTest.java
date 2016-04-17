package net.oneandone.concierge.resource.helper;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ResourceIdentifierTest {
    @Test
    public void testParse() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");
        assertNotNull(identifier);
        assertEquals(new String[] { "a", "b", "c" }, identifier.get());
    }

    @Test
    public void testGetResolverPath() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");
        assertEquals(new String[] { "a" }, identifier.hierarchy());
    }

    @Test
    public void testIsFinalPart() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");

        assertTrue(identifier.hasNextScope());
        assertTrue(identifier.hasNextScope());
        assertFalse(identifier.next().hasNextScope());
    }

    @Test
    public void testHasNextPart() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b/c");

        assertTrue(identifier.hasNextScope());
        final ResourceIdentifier nextPart = identifier.next();

        assertEquals(new String[] { "a", "b", "c" }, identifier.get());
    }

    @Test
    public void testGetNextPart() throws Exception {
        assertTrue(ResourceIdentifier.parse("a/b/c").hasNextScope());
        assertFalse(ResourceIdentifier.parse("a").hasNextScope());
        assertFalse(ResourceIdentifier.parse("a/b").hasNextScope());
    }

    @Test
    public void testIsElementIdentifier() throws Exception {
        assertTrue(ResourceIdentifier.parse("a/b/c").hasElementIdentifier());
        assertFalse(ResourceIdentifier.parse("a").hasElementIdentifier());
        assertTrue(ResourceIdentifier.parse("a/b").hasElementIdentifier());
        assertTrue(ResourceIdentifier.parse("a/b/c/d").hasElementIdentifier());
    }

    @Test
    public void testGetGroupOrExtensionIdentifier() throws Exception {
        final ResourceIdentifier identifier = ResourceIdentifier.parse("a/b");
        assertEquals("a", identifier.groupIdentifier());
    }

}