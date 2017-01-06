/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2013-2016 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

/**
 * A repository for host-specific configuration settings, retrievable by host name (or an alias).
 * This can be implemented by an application and passed to {@link JSch#setConfigRepository(ConfigRepository)}.
 * @since 0.1.50 
 */
public interface ConfigRepository {

  /**
   * Returns the configuration for a specific host name (or host name alias).
   * @param host The host name. Can also be {@code ""}, which are default settings used for some of the properties.
   */
  public Config getConfig(String host);

  /**
   * A configuration for connections to a remote host name (or alias).
   */
  public interface Config {
    /**
     * The actual host name to use for connecting. {@code null} means to use the host name indicated
     * by the parameter to {@link JSch#getSession}.  
     */
    public String getHostname();
    /**
     * The user name to use for connecting.  {@code null} means to use the user name
     *  indicated by the parameter to {@link JSch#getSession}.
     */
    public String getUser();
    /**
     *  The port number to use for connecting. {@code -1} means to use the port number
     *  indicated by the parameter to {@link JSch#getSession(String, String, int)}, or the default port.
     */
    public int getPort();
    /**
     * A configuration value for a named key, as a string.
     * {@code null} means to use that key's default value.
     */
    public String getValue(String key);
    /**
     * A list of configuration values for a named key, as an array of strings.
     * {@code null} means to use that key's default list of values.
     */
    public String[] getValues(String key);
  }

  /**
   * An implementation of {@link Config} which returns {@code null} (or {@code -1}) for every method.
   */
  static final Config defaultConfig = new Config() {
    public String getHostname() {return null;}
    public String getUser() {return null;}
    public int getPort() {return -1;}
    public String getValue(String key) {return null;}
    public String[] getValues(String key) {return null;}
  };

  /**
   * A dummy ConfigRepository, where each host has an empty configuration.
   */
  static final ConfigRepository nullConfig = new ConfigRepository(){
    public Config getConfig(String host) { return defaultConfig; }
  };
}
