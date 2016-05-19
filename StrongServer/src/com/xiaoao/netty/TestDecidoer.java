package com.xiaoao.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;

public class TestDecidoer extends DelimiterBasedFrameDecoder {
	public TestDecidoer(int maxFrameLength) {
		super(maxFrameLength, lineDelimiter());
	}

	public static ChannelBuffer[] lineDelimiter() {
		return new ChannelBuffer[] {
				ChannelBuffers.wrappedBuffer(new byte[] { '\r', '\n' }),
				ChannelBuffers.wrappedBuffer(new byte[] { '\n' }), };
	}

	
}
