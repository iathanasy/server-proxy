/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.icss.utils;

import io.netty.channel.Channel;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * channel utils
 */
public class ChannelUtils {

    private ChannelUtils() {
        throw new IllegalStateException(ChannelUtils.class.getName());
    }

    /**
     * get local address
     *
     * @param channel channel
     * @return local address
     */
    public static String getLocalAddress(Channel channel) {
        return getHost(((InetSocketAddress) channel.localAddress()).getAddress());
    }

    /**
     * get remote address
     *
     * @param channel channel
     * @return remote address
     */
    public static String getRemoteAddress(Channel channel) {
        return getHost(((InetSocketAddress) channel.remoteAddress()).getAddress());
    }

    /**
     * channel to address
     *
     * @param channel channel
     * @return address
     */
    public static Host toAddress(Channel channel) {
        InetSocketAddress socketAddress = ((InetSocketAddress) channel.remoteAddress());
        return new Host(getHost(socketAddress.getAddress()), socketAddress.getPort());
    }

    /**
     * get local address
     *
     * @param channel channel
     * @return address
     */
    public static Host toLocalAddress(Channel channel) {
        InetSocketAddress socketAddress = ((InetSocketAddress) channel.localAddress());
        return new Host(getHost(socketAddress.getAddress()), socketAddress.getPort());
    }



    /**
     * docker & kubernetes
     */
    public static final boolean KUBERNETES_MODE = false;
    /**
     * get host
     * @return host
     */
    public static String getHost(InetAddress inetAddress) {
        if (inetAddress != null) {
            if (KUBERNETES_MODE) {
                String canonicalHost = inetAddress.getCanonicalHostName();
                String[] items = canonicalHost.split("\\.");
                if (items.length == 6 && "svc".equals(items[3])) {
                    return String.format("%s.%s", items[0], items[1]);
                }
                return canonicalHost;
            }
            return inetAddress.getHostAddress();
        }
        return null;
    }

}
