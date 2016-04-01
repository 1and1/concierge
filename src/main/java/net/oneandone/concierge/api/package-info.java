/**
 * Groups contain elements of equal type.
 * An element may have an extension for its specific type.
 * Elements can also have sub-groups that aren't contained within the object but should be resolved by a group resolver.
 *
 * Data model:
 *
 * - Group 1
 *     - Element 1
 *        - Extension
 *     - Element 2
 * - Group 2
 *     - Element 3
 *        - Group 3
 *           - Element 4
 */
package net.oneandone.concierge.api;