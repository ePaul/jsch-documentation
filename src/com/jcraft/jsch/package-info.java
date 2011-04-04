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
 */
package com.jcraft.jsch;