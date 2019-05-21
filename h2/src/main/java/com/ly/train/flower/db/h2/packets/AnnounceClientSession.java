package com.ly.train.flower.db.h2.packets;

import com.ly.train.flower.db.api.support.SizeConstants;
import com.ly.train.flower.db.h2.decoding.IoUtils;
import com.ly.train.flower.db.h2.protocol.CommandCodes;
import java.io.DataOutputStream;
import java.io.IOException;


public class AnnounceClientSession extends ClientToServerPacket {


  private final String sessionId;

  public AnnounceClientSession(String sessionId) {
    super();
    this.sessionId = sessionId;
  }

  @Override
  public void writeToStream(DataOutputStream stream) throws IOException {
    stream.writeInt(CommandCodes.SESSION_SET_ID.getCommandValue());
    IoUtils.writeString(stream, sessionId);
  }

  @Override
  public int getLength() {
    return SizeConstants.INT_SIZE + // request type
        SizeConstants.sizeOf(sessionId) + 0;
  }
}
