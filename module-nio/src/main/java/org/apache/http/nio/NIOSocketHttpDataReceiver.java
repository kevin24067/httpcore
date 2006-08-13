/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Copyright 1999-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Data receiver based on sockets and <code>java.nio</code>.
 *
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 *
 * <!-- empty lines above to avoid 'svn diff' context problems -->
 * @version $Revision$
 * 
 * @since 4.0
 */
public class NIOSocketHttpDataReceiver extends NIOHttpDataReceiver {

    private final SocketChannel channel;
    
    public NIOSocketHttpDataReceiver(final Socket socket, int buffersize) throws IOException {
        super();
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (socket.getChannel() == null) {
            throw new IllegalArgumentException("Socket does not implement NIO channel");
        }
        this.channel = socket.getChannel();
        if (buffersize < 0) {
            buffersize = socket.getReceiveBufferSize();
        }
        if (buffersize < 1024) {
            buffersize = 1024;
        }
        initBuffer(buffersize);
    }
    
    public void reset(final HttpParams params) {
        if (HttpConnectionParams.getSoTimeout(params) > 0) {
        	throw new IllegalStateException("Socket timeout not supported by NIO connections");
        }
        super.reset(params); 
    }

    protected int fillBuffer(final ByteBuffer dst) throws IOException {
        if (dst == null) {
            throw new IllegalArgumentException("Byte buffer may not be null");
        }
        return this.channel.read(dst);
    }
  
    public boolean isDataAvailable(int timeout) throws IOException {
        return hasDataInBuffer();
    }    
        
}
