/**
 * Java Secure Channel - main package.
 * This package contains all types applications using
 * the library have to know (and lots they don't have to know).
 *
 * <p>Here is an overview about the most important ones:</p>
 * <dl>
 *  <dt>{@link com.jcraft.jsch.JSch}</dt>
 *  <dd>The starting point, used to create sessions and manage identities.</dd>
 *  <dt>{@link com.jcraft.jsch.Session}</dt>
 *  <dd>A connection to a SSH server. Used to configure settings,
 *     port forwardings and to open channels.</dd>
 * </dt>
 *
 * <p>The string <em>Usually not to be used by applications.</em>
 *   at the beginning of a class or interface description means
 *   that this type is used internally by the library, and normally
 *   there is no need for an application to directly use
 *   (or implement/extend) this type.  It may be needed if you need to
 *   extend functionality, then you normally will also have to change some
 *   {@linkplain com.jcraft.jsch.JSch#setConfig configuration options}.
 * </p>
 */
package com.jcraft.jsch;